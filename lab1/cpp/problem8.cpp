#include <fstream>
#include <iostream>

using namespace std;

int main() {
  ifstream infile("input8.txt");
  string s;
  infile >> s;
  int n = s.size();
  long long prod = 1, res = 0;
  int prod_len = 0;
  for (int i = 0; i < n; ++i) {
    prod *= (s[i] - '0');
    ++prod_len;
    if (prod == 0) {
      prod = 1;
      prod_len = 0;
      continue;
    }
    while (prod_len > 13) {
      prod /= (s[i - 13] - '0');
      --prod_len;
    }
    if (prod_len == 13) {
      res = (res < prod) ? prod : res;
    }
  }
  cout << res << endl;
  return 0;
}
