import random
import requests


def make_orgnummer():
    r = random.randint(80000000, 99999999)

    r_str = str(r)

    w = "32765432"

    sum = 0

    for i, c in enumerate(r_str):
        sum += (int(c) * int(w[i]))

    control_digit = (sum % 11) - 6

    final = r_str + str(control_digit)

    req = requests.get(
        "https://modapp-q2.adeo.no/ereg/api/v1/organisasjon/"
        + final +
        "?inkluderHierarki=false&inkluderHistorikk=false")

    req2 = requests.get(
        "https://modapp-q0.adeo.no/ereg/api/v1/organisasjon/"
        + final +
        "?inkluderHierarki=false&inkluderHistorikk=false")

    if control_digit == 1 or control_digit < 0 or req.status_code == 200 or req2.status_code == 200:
        return make_orgnummer()
    return final


print(make_orgnummer())
