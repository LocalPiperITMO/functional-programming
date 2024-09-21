#include <cmath>
#include <iostream>
#include <vector>
using namespace std;

int divsum(int n) {
  if (n < 2)
    return 0;
  int sum = 1;
  for (int i = 2; i * i <= n; ++i) {
    if (n % i == 0) {
      sum += i;
      if (i != n / i) {
        sum += n / i;
      }
    }
  }
  return sum;
}

void generate(vector<int> &abundantNumbers, int limit) {
  for (int i = 12; i <= limit; ++i)
    if (divsum(i) > i)
      abundantNumbers.push_back(i);
}

int main() {
  const int LIMIT = 28213;
  vector<int> abundantNumbers;
  generate(abundantNumbers, LIMIT);
  vector<bool> canBeWritten(LIMIT + 1, false);

  for (int i = 0; i < abundantNumbers.size(); ++i) {
    for (int j = i; j < abundantNumbers.size(); ++j) {
      long long sum = abundantNumbers[i] + abundantNumbers[j];
      if (sum <= LIMIT) {
        canBeWritten[sum] = true;
      } else {
        break;
      }
    }
  }
  long long res = 0;
  for (long long i = 1; i <= LIMIT; ++i) {
    if (!canBeWritten[i]) {
      res += i;
    }
  }
  cout << res << endl;
  return 0;
}
