type Field = {
  key: string;
  value: unknown;
};

type CompareField = {
  left: Field;
  right: Field;
};

type CompareItem = {
  item: any;
  key: string;
};

const compareFields = (left: CompareItem, right: CompareItem) => ({
  equal:
    (!left.item[left.key] && !right.item[right.key]) ||
    (!!left.item[left.key] && left.item[left.key].toUpperCase() === right.item[right.key]),
  comparedField: {
    left: {
      key: left.key,
      value: left.item[left.key],
    },
    right: {
      key: right.key,
      value: right.item[right.key],
    },
  },
});

const compare = (left: Person, right: Person) => {
  const mismatchFields: CompareField[] = [];

  function checkMismatch<T, I>(leftKey: keyof T, rightKey: keyof I) {
    const { equal, comparedField } = compareFields(
      { item: left, key: leftKey.toString() },
      { item: right, key: rightKey.toString() }
    );
    if (!equal) {
      mismatchFields.push(comparedField);
    }
  }

  checkMismatch<Person, Person>('ident', 'ident');
  checkMismatch<Person, Person>('fornavn', 'fornavn');
  checkMismatch<Person, Person>('mellomnavn', 'mellomnavn');
  checkMismatch<Person, Person>('etternavn', 'etternavn');
  checkMismatch<Person, Person>('foedselsdato', 'foedselsdato');

  checkMismatch<Adresse, Adresse>('gatenavn', 'gatenavn');
  checkMismatch<Adresse, Adresse>('kommunenummer', 'kommunenummer');
  checkMismatch<Adresse, Adresse>('poststed', 'poststed');
  checkMismatch<Adresse, Adresse>('postnummer', 'postnummer');

  return {
    mismatchFields,
    isMismatch: mismatchFields.length > 0,
  };
};

export default {
  compare,
};
