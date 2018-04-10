const schema = {
    "type": "object",
    "properties": {
        "persons": {
            "type": "array",
            "minItems": 3,
            "maxItems": 20,
            "items": {
                "type": "object",
                "properties": {
                    id: {
                        type: 'string',
                        unique: true,
                        minLength: 1,
                        maxLength: 3,
                        pattern: '\\d+'
                    },
                    fnr: {
                        type: 'string',
                        pattern: '\\d{11}'
                    },
                    fornavn: {
                        type: 'string',
                        pattern: '[a-z]{1,9}'
                    },
                    etternavn: {
                        type: 'string',
                        pattern: '[a-z]{1,9}'
                    },
                    kjonn: {
                        type: 'string',
                        pattern: '[mk]{1}'
                    },
                    adresse: {
                        type: "string",
                        pattern: '[a-z]{1,9}'
                    }
                },
                "required": ["id","fnr", "kjonn", "fornavn", "etternavn", "adresse"]
            }
        }
    },
    "required": ["persons"]
};

module.exports = schema;