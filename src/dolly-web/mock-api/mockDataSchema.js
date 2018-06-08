const schema = {
	type: 'object',
	properties: {
		persons: {
			type: 'array',
			minItems: 10,
			maxItems: 10,
			items: {
				type: 'object',
				properties: {
					id: {
						type: 'integer',
						autoIncrement: true
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
						type: 'string',
						pattern: '[a-z]{1,9}'
					},
					gruppeId: {
						tyoe: 'string',
						pattern: '[1-5]'
					}
				}
			}
		},
		grupper: {
			type: 'array',
			minItems: 5,
			maxItems: 5,
			items: {
				type: 'object',
				properties: {
					id: {
						type: 'integer',
						autoIncrement: true
					},
					navn: {
						type: 'string',
						pattern: 'Gruppe^[1-9][0-9]?$'
					},
					team: {
						type: 'string',
						pattern: 'FREG|FO'
					},
					eier: {
						type: 'string',
						faker: 'name.findName'
					},
					hensikt: {
						type: 'string',
						faker: 'lorem.sentence'
					},
					personer: {
						type: 'string',
						pattern: '32|38|45|50|60'
					},
					env: {
						type: 'string',
						pattern: 't5, t6|t6, t7|t5, t6, t7'
					}
				}
			}
		},
		teams: {
			type: 'array',
			minItems: 2,
			maxItems: 10,
			items: {
				type: 'object',
				properties: {
					id: {
						type: 'integer',
						autoIncrement: true
					},
					navn: {
						type: 'string',
						pattern: '[a-z]{1,9}'
					},
					beskrivelse: {
						type: 'string',
						pattern: '[a-z]{1,9}'
					}
				}
			}
		},
		bruker: {
			type: 'object',
			properties: {
				id: {
					type: 'integer',
					autoIncrement: true
				},
				navn: 'testBruker'
			}
		}
	}
}

module.exports = schema
