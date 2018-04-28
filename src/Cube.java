import java.util.*;

public class Cube {

    final int size;

    private final char[][][] value; //Хранит значение граней

    private final Map<Character, Integer> faces = new HashMap<>();

    private static final char[] colors = {'W', 'Y', 'R', 'O', 'B', 'G'};

    private static final Character[] facesName = {'F', 'B', 'L', 'R', 'D', 'U'};

    public Cube(int size) {
        this.size = size;
        value = new char[6][size][size];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    value[i][j][k] = colors[i];
                }
            }
            faces.put(facesName[i], i);
        }
    }

    private void turn(char name, int direction) { //Поворачивает грань против(direction == 0) и по(direction == 1) часовой стрелки грань name
        if (!(Arrays.asList(facesName).contains(name))) throw new IllegalArgumentException();
        char[][] rotate = new char[size][size];
        switch (direction) {
            case 0: {
                for (int i = 0; i < size; i++) for (int j = 0; j < size; j++) {
                    rotate[i][j] = value[faces.get(name)][j][size - 1 - i]; //против часовой
                }
                break;
            }
            case 1: {
                for (int i = 0; i < size; i++) for (int j = 0; j < size; j++) {
                    rotate[i][j] = value[faces.get(name)][size - 1 - j][i]; //по часовой
                }
                break;
            }
            default: throw new IllegalArgumentException();
        }
        for (int i = 0; i < size; i++) for (int j = 0; j < size; j++) {
            value[faces.get(name)][i][j] =rotate[i][j];
        }
    }

    public void rotateCube(int direction) { //Поворачивает кубик. У пользователь перед глазами всегда фасад
        char[] names;
        switch (direction) {
            case 0: { //Вправо
                names = new char[]{'F', 'R', 'B', 'L', 'U', 'D'};
                break;
            }
            case 1: { //Влево
                names = new char[]{'L', 'B', 'R', 'F', 'D', 'U'};
                break;
            }
            case 2: { //Вверх
                names = new char[]{'F', 'U', 'B', 'D', 'L', 'R'};
                this.turn('U', 0);
                this.turn('U', 0);
                this.turn('B', 0);
                this.turn('B', 0);
                break;
            }
            case 3: { //Вниз
                names = new char[]{'D', 'B', 'U', 'F', 'R', 'L'};
                this.turn('D', 0);
                this.turn('D', 0);
                this.turn('B', 0);
                this.turn('B', 0);
                break;
            }
            case 4: { //Против часовой
                names = new char[]{'L', 'D', 'R', 'U', 'F', 'B'};
                for (int i = 0; i < 4; i++) this.turn(names[i], 0);
                break;
            }
            case 5: { //По часовой
                names = new char[]{'U', 'R', 'D', 'L', 'B', 'F'};
                for (int i = 0; i < 4; i++) this.turn(names[i], 1);
                break;
            }
            default: throw new IllegalArgumentException();
        }
        //Переименовка 4 граней
        int temp = faces.get(names[3]);
        for (int i = 3; i > 0; i--) faces.put(names[i], faces.get(names[i - 1]));
        faces.put(names[0], temp);
        //Поворот 2 граней на 90 градусов
        this.turn(names[4], 0);
        this.turn(names[5], 1);
    }

    public void rotateFace(int axis, int number, int direction) { //Поворачивает грань
        //У пользователь перед глазами всегда фасад
        //axis(0 - X, 1 - Y, 2 - Z)(O - левый верхний ближний угол)(Ось X - вправо, Y - вниз, Z - вглубь)
        //number - номер грани от 0 по size - 1
        //direction(0 - вправо || вверх || против часовой; 1 - влево || вниз || по часовой)
        if (!(axis >= 0 && axis <= 2 && number >= 0 && number < size && (direction == 0 || direction == 1)))
            throw new IllegalArgumentException();
        int recover;
        switch (axis) {
            case 1: {
                this.rotateCube(4);
                recover = 5;
                break;
            }
            case 2: {
                this.rotateCube(1);
                recover = 0;
                break;
            }
            default: recover = -1;
        }
        char[] names;
        switch (direction) {
            case 0: {
                names = new char[]{'F', 'U', 'B', 'D'};
                break;
            }
            case 1: {
                names = new char[]{'F', 'D', 'B', 'U'};
                break;
            }
            default: names = new char[0];
        }
        char[] temp = new char[size];
        for (int i = 0; i < size; i++) temp[i] = value[faces.get(names[3])][i][number];
        for (int i = 0; i < size; i++) {
            value[faces.get(names[3])][i][number] = value[faces.get(names[2])][size - 1 - i][size - 1 - number];
        }
        for (int i = 0; i < size; i++) {
            value[faces.get(names[2])][size - 1 - i][size - 1 - number] = value[faces.get(names[1])][i][number];
        }
        for (int i = 0; i < size; i++) {
            value[faces.get(names[1])][i][number] = value[faces.get(names[0])][i][number];
        }
        for (int i = 0; i < size; i++) {
            value[faces.get(names[0])][i][number] = temp[i];
        }
        if (number == 0) this.turn('L', direction); //вообще '(0 + direction) % 2'
        if (number == size - 1) this.turn('R', (1 + direction) % 2);
        if (recover != -1) this.rotateCube(recover);
    }

    public void confuse() { //Случайным образом "перемешивает" кубик
        Random random = new Random();
        for (int i = 0; i < 30 * size; i++) {
            this.rotateFace(random.nextInt(3), random.nextInt(size), random.nextInt(2));
        }
    }

    public char[][] getFace(char name) {
        return value[faces.get(name)];
    }

    public void printlnCube() {

        for (int i = 0; i < size; i++) {
            System.out.print("                ");
            for (int j = 0; j < size; j++) System.out.print(value[faces.get('U')][i][j] + " ");
            System.out.println();
        }
        char[] middle = {'B', 'L', 'F', 'R'};
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < 4; k++) {
                for (int j = 0; j < size; j++) System.out.print(value[faces.get(middle[k])][i][j] + " ");
                System.out.print("  ");
            }
            System.out.println();
        }
        for (int i = 0; i < size; i++) {
            System.out.print("                ");
            for (int j = 0; j < size; j++) System.out.print(value[faces.get('D')][i][j] + " ");
            System.out.println();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int k = 0; k < 6; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++)
                    result.append(value[faces.get(facesName[k])][i][j]);
            }
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != this.getClass()) return false;

        Cube c = (Cube) other;

        if (c.size != this.size) return false;

        boolean cubeIsEquals = false;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                if (Objects.equals(this.toString(), c.toString())) {
                    cubeIsEquals = true;
                    break;
                }
                c.rotateCube(5);
            }
            if (cubeIsEquals) break;
            c.rotateCube((i % 2) + 1);
        }
        return cubeIsEquals;
    }

    @Override
    public int hashCode() {
        return this.size;
    }
}