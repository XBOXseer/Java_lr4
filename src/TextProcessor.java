import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Клас, що представляє букву
class Letter {
    private char character;

    public Letter(char character) {
        this.character = character; // Ініціалізуємо літеру
    }

    public char getCharacter() {
        return character; // Повертаємо символ літери
    }
}

// Клас, що представляє слово
class Word {
    private List<Letter> letters;

    public Word(String word) {
        letters = new ArrayList<>(); // Ініціалізація списку літер
        for (char c : word.toCharArray()) {
            letters.add(new Letter(c)); // Додавання кожного символу як об'єкту Letter
        }
    }

    public String getWord() {
        StringBuilder sb = new StringBuilder();
        for (Letter letter : letters) {
            sb.append(letter.getCharacter()); // Формуємо слово з літер
        }
        return sb.toString();
    }

    public int length() {
        return letters.size(); // Повертаємо довжину слова
    }

    public char getFirstCharacter() {
        return letters.get(0).getCharacter(); // Повертаємо перший символ слова
    }
}

// Клас, що представляє речення
class Sentence {
    private List<Object> elements; // Можуть бути слова або знаки пунктуації

    public Sentence(String sentence) {
        elements = new ArrayList<>(); // Ініціалізація списку елементів
        String[] parts = sentence.split("(?=[.,!?])|\s+"); // Розділяємо за пунктуацією або пробілами
        for (String part : parts) {
            if (part.matches("[.,!?]")) {
                elements.add(part); // Додаємо знаки пунктуації
            } else if (!part.isBlank()) {
                elements.add(new Word(part)); // Створюємо об'єкти Word для слів
            }
        }
    }

    public List<Object> getElements() {
        return elements; // Повертаємо список елементів речення
    }

    public void updateElements(List<Object> updatedElements) {
        elements.clear(); // Очищуємо поточні елементи
        elements.addAll(updatedElements); // Оновлюємо новими елементами
    }

    public String getSentence() {
        StringBuilder sb = new StringBuilder();
        for (Object element : elements) {
            if (element instanceof Word) {
                sb.append(((Word) element).getWord()).append(" "); // Додаємо слова з пробілом
            } else {
                sb.append(element); // Додаємо знаки пунктуації
            }
        }
        return sb.toString().trim(); // Повертаємо речення у вигляді рядка
    }
}

// Клас, що представляє текст
class Text {
    private List<Sentence> sentences;

    public Text(String text) {
        sentences = new ArrayList<>(); // Ініціалізація списку речень
        text = text.replaceAll("[\t\s]+", " ").trim(); // Замінюємо табуляції/пробіли на один пробіл
        String[] sentenceArray = text.split("(?<=[.!?])\s+"); // Розбиваємо текст на речення
        for (String sentence : sentenceArray) {
            sentences.add(new Sentence(sentence)); // Створюємо об'єкти Sentence
        }
    }

    public List<Sentence> getSentences() {
        return sentences; // Повертаємо список речень
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (Sentence sentence : sentences) {
            sb.append(sentence.getSentence()).append(" "); // Поєднуємо всі речення в текст
        }
        return sb.toString().trim(); // Повертає текст у вигляді рядка
    }
}

public class TextProcessor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введіть текст(анг.розкладка):");
        String inputText = scanner.nextLine();

        Text text = new Text(inputText); // Створення об'єкта Text

        while (true) {
            System.out.println("Введіть довжину приголосного слова для видалення (або натисніть Enter для виходу):");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                System.out.println("Вихід...");
                break;
            }

            int wordLength;
            try {
                wordLength = Integer.parseInt(input); // Перетворення довжини слова у число
                if (wordLength <= 0) {
                    throw new NumberFormatException("Довжина слова повинна бути > 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Неправильне введеня. Будь-ласка введіть позитивне число.");
                continue;
            }

            boolean wordsRemoved = removeWords(text, wordLength); // Спроба видалення слів

            if (!wordsRemoved) {
                System.out.println("Немає слів заданої довжини " + wordLength + ", які починаються з приголосної букви.");
                System.out.println("Що обереш ти?:");
                System.out.println("1. Спробувати іншу задану довжину");
                System.out.println("2. Ввести новий текст");
                System.out.println("Натисніть Enter для виходу.");

                String choice = scanner.nextLine();

                if (choice.isEmpty()) {
                    System.out.println("Вихід...");
                    break;
                } else if (choice.equals("1")) {
                    continue;
                } else if (choice.equals("2")) {
                    System.out.println("Введіть ваш новий текст:");
                    inputText = scanner.nextLine();
                    text = new Text(inputText);
                } else {
                    System.out.println("Неправильне введення. Вихід з програми.");
                    break;
                }
            } else {
                System.out.println("Оновлений текст:");
                System.out.println(text.getText());
            }
        }

        scanner.close();
    }

    private static boolean removeWords(Text text, int wordLength) {
        String vowels = "AEIOUaeiou"; // Визначення голосних літер
        boolean wordRemoved = false;

        for (Sentence sentence : text.getSentences()) {
            List<Object> elements = new ArrayList<>(sentence.getElements()); // Копія елементів
            List<Object> newElements = new ArrayList<>(); // Підготовка оновлених елементів

            for (Object element : elements) {
                if (element instanceof Word) {
                    Word word = (Word) element;
                    // Перевірка, чи відповідає слово критеріям
                    if (word.length() == wordLength && vowels.indexOf(word.getFirstCharacter()) == -1) {
                        wordRemoved = true; // Позначення слова як видаленого
                    } else {
                        newElements.add(word); // Залишаємо слово
                    }
                } else {
                    newElements.add(element); // Залишаємо знаки пунктуації
                }
            }

            sentence.updateElements(newElements);
        }

        return wordRemoved; // Повертає, чи було видалено хоча б одне слово
    }
}