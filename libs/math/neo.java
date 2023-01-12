package libs.math;

public class neo {
	public int mod(int N, int M) {
		while (N < 0) N += M;
		return N%M;
	}
	public class Vec2 {
		public double x, y;
		public Vec2(double N, double M) {
			x = N;
			y = M;
		}
		public Vec2 scale(double scalar) {
			return new Vec2(x*scalar, y*scalar);
		}
		public Vec2 add(Vec2 v2) {
			return new Vec2(x+v2.x, y+v2.y);
		}
    public Vec2 add(double p_x, double p_y) {
      return new Vec2(x+p_x, y+p_y);
    }
		public Vec2 subtract(Vec2 v2) {
			return add(v2.scale(-1));
		}
		public String toString() {
			return String.format("(%.3f, %.3f)", x, y);
		}
	};
	public class Vector {
		int size, index = 0;
		double[] values;
		public Vector(int s) {
				size = s;
				values = new double[s];
				this.fill(0);
		}
		public Vector(int s, double[] v) {
			size = s;
			values = new double[s];
			for (int i = 0; i < Math.min(v.length, size); i++) {
				values[i] = v[i];
			}
		}
		public void push(double v) {
			if (index >= size) {
				resize(size+1);
			}
			values[index++] = v;
		}
		public void fill(double ff) {
			for (int i = 0; i < size; i++) {
				values[i] = ff;
			}
		}
		public Vector scale(double scalar) {
			Vector ret = new Vector(this.size);
			for (int i = 0; i < this.size; i++) {
				ret.push(values[i]*scalar);
			}
			return ret;
		}
		public Vector add(Vector v2) {
			assert(this.size == v2.size);
			Vector sum = new Vector(this.size);
			for (int i = 0; i < this.size; i++) {
				sum.values[i] = v2.values[i] + this.values[i];
			}
			return sum;
		}
		public Vector sub(Vector v2) {
			assert(this.size == v2.size);
			return this.add(v2.scale(-1.0));
		}
		public double get(int index) {
			assert(index >= 0 && index < size);
			return this.values[index];
		}
		public void insert(int index, int v) {
			assert(index >= 0 && index < size);
			this.values[index] = v;
		}
		public void resize(int n_s) {
			size = n_s;
			double[] tmp = new double[size];
			for (int i = 0; i < index; i++) {
				tmp[i] = values[i];
			}
			values = tmp;
		}
		public String toString() {
			String printed = "";
			for (int i = 0; i < size; i++) {
				printed = printed.concat(String.format("[%.3f]", values[i])); 
			}
			return printed;
		}
	}

	public class Matrix {
		int rows, cols;
		Vector[] m;
		public Matrix(int r, int c) {
			rows = r;
			cols = c;
			m = new Vector[rows];
			for (int i = 0; i < rows; i++) {
				m[i] = new Vector(cols);
			}
		}
		public void fill(double v) {
			for (int i = 0; i < rows; i++) {
				m[i].fill(v);
			}
		}
		public String toString() {
			String ret = "";
			for (int i = 0; i < rows; i++) {
				ret = ret.concat(m[i].toString() + "\n");
			}
			return ret;
		}
		Vector get_col(int c) {
			assert(c >= 0 && c < cols);
			Vector col = new Vector(rows);
			for (int i = 0; i < rows; i++) {
				col.push(m[i].get(c));
			}
			return col;
		}
		Vector get(int r) {
			return this.m[r];
		}
	};

	public double dot(Vector v1, Vector v2) {
		double dp = 0.0;
		assert(v1.size == v2.size);
		for (int i = 0; i < v1.size; i++) {
			dp += (v1.get(i) * v2.get(i));
		}
		return dp;
	}

	public Matrix transpose(Matrix m1) {
		Matrix tn = new Matrix(m1.cols, m1.rows);
		for (int i = 0; i < m1.cols; i++) {
			tn.m[i] = m1.get_col(i);
		}
		return tn;
	}

	public Matrix add(Matrix m1, Matrix m2) {
		assert(m1.cols == m2.cols && m1.rows == m2.rows);
		Matrix sum = new Matrix(m1.rows, m1.cols);
		for (int i = 0; i < m1.cols; i++) {
			sum.m[i] = m1.get(i).add(m2.get(i));
		}
		return sum;
	}

	public Matrix scale(Matrix m1, double scalar) {
		Matrix scaled = new Matrix(m1.rows, m1.cols);
		for (int i = 0; i < m1.rows; i++) {
			scaled.m[i] = m1.m[i].scale(scalar);
		}
		return scaled;
	}
	
	public Matrix sub(Matrix m1, Matrix m2) {
		return add(m1, scale(m2, -1));
	}
	
	public Matrix matmul(Matrix m1, Matrix m2) {
		assert(m1.cols == m2.rows);
		Matrix mm = new Matrix(m1.rows, m2.cols);
		for (int r = 0; r < m1.rows; r++) {
			for (int c = 0; c < m2.cols; c++) {
				mm.m[r].push(dot(m1.get(r), m2.get_col(c)));
			}
		}
		return mm;
	}
}
