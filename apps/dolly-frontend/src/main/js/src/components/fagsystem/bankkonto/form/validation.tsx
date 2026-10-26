import * as Yup from 'yup'
import { ifPresent, messages, requiredString } from '@/utils/YupValidations'
import * as _ from 'lodash-es'
import {
	finnKontoregisterLandkode,
	landkodeIsoMapping,
} from '@/service/services/kontoregister/landkoder'

const validInputOrCheckboxTest = (val, checkboxPath, feilmelding, inputValidation) => {
	return val.test('is-input-or-checkbox', (value, testContext) => {
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		if (value) {
			if (inputValidation) {
				const inputError = inputValidation(value, testContext)
				if (inputError) {
					return testContext.createError({ message: inputError })
				}
			}
			return true
		}

		const path = testContext.path.substring(0, testContext.path.lastIndexOf('.'))

		const checkbox = _.get(fullForm, `${path}.${checkboxPath}`)

		if (!checkbox) {
			return testContext.createError({ message: feilmelding })
		}

		return true
	})
}

// from - begynner med 0
const replaceSubstringAtPos = (str, from, to, replaceWith) => {
	return str.substring(0, from) + replaceWith + str.substring(to + 1)
}

const validateIban = (kontonummer, testContext) => {
	if (!kontonummer) {
		return messages.required
	}

	if (kontonummer && (kontonummer.length < 1 || kontonummer.length > 36)) {
		return 'Kontonummer kan være mellom 1 og 36 tegn'
	}
	if (!/^[A-Z0-9]*$/.test(kontonummer)) {
		return 'Kontonummer kan kun bestå av tegnene A-Z eller 0-9'
	}

	const kontoregisterLandkode = finnKontoregisterLandkode(testContext.parent?.landkode)
	if (!kontoregisterLandkode?.kreverIban) {
		return ''
	}

	const landkode = kontoregisterLandkode.landkode
	if (kontonummer.substring(0, 2) !== landkode) {
		return `Feil landkode i kontonummer. Den bør være ${landkode} (${landkode}${kontonummer})`
	}

	if (kontoregisterLandkode.ibanLengde && kontonummer.length !== kontoregisterLandkode.ibanLengde) {
		return `Kontonummer for ${landkode} må være ${kontoregisterLandkode.ibanLengde} tegn`
	}

	return ''
}

const validateSwift = (val) => {
	return val.test('swift-validering', (val, testContext) => {
		const context = testContext.options.context
		const parent = testContext.parent ?? context.parent
		const landkode = parent?.landkode

		let mappedLandkode = null

		if (landkode) {
			const isoLandkode = landkodeIsoMapping[landkode]
			mappedLandkode = isoLandkode ? isoLandkode : landkode.substring(0, 2)
		}

		const value = parent?.swift
		if (!value) {
			if (mappedLandkode) {
				const kontoregisterLandkode = finnKontoregisterLandkode(mappedLandkode)
				if (kontoregisterLandkode?.kreverBicOgBankkode) {
					return testContext.createError({
						message: `${kontoregisterLandkode.land} krever både BIC og bankkode`,
					})
				}
				if (kontoregisterLandkode?.kreverIban) {
					return testContext.createError({ message: messages.required })
				}
			}

			return true
		}

		if (value.length !== 11 && value.length !== 8) {
			return testContext.createError({ message: 'Må være enten 8 eller 11 tegn' })
		}

		const swiftFormatExplain =
			'BBBBLLCCDDD - hvor BBBB er bankkode (4 tegn fra A-Z), LL er landkode (2 tegn), CC er sted (2 tegn fra 0-9, A-Z), og DDD er bransjekode 3 tegn fra 0-9, A-Z.'

		if (!/^[A-Z]{6}[0-9A-Z]{2}([0-9A-Z]{3})?$/.test(value)) {
			return testContext.createError({
				message: `Ugyldig format. ${swiftFormatExplain}`,
			})
		}

		if (mappedLandkode && value.substring(4, 6) !== mappedLandkode) {
			return testContext.createError({
				message:
					`Feil landkode i SWIFT. Den bør være ${mappedLandkode} (${replaceSubstringAtPos(
						value,
						4,
						5,
						mappedLandkode,
					)}). \n\n` + `Swift format: ${swiftFormatExplain}`,
			})
		}
		return true
	})
}

const validateBankkode = (schema: Yup.StringSchema<string | null | undefined>) => {
	return schema.test('bankkode-validering', (value, testContext) => {
		const kontoregisterLandkode = finnKontoregisterLandkode(testContext.parent?.landkode)
		if (!kontoregisterLandkode?.bankkodeLengde) {
			return true
		}

		if (!value) {
			if (kontoregisterLandkode.kreverBicOgBankkode) {
				return testContext.createError({
					message: `${kontoregisterLandkode.land} krever både BIC og bankkode`,
				})
			}
			if (kontoregisterLandkode.kreverBankkode) {
				return testContext.createError({ message: messages.required })
			}
			return true
		}

		if (value.length !== kontoregisterLandkode.bankkodeLengde) {
			return testContext.createError({
				message: `Bankkode for ${kontoregisterLandkode.landkode} må være ${kontoregisterLandkode.bankkodeLengde} tegn`,
			})
		}
		return true
	})
}

export const bankkontoValidation = {
	bankkonto: ifPresent(
		'$bankkonto',
		Yup.object({
			utenlandskBankkonto: ifPresent(
				'$bankkonto.utenlandskBankkonto',
				Yup.object().shape({
					kontonummer: validInputOrCheckboxTest(
						Yup.string(),
						'tilfeldigKontonummer',
						messages.required,
						validateIban,
					),
					tilfeldigKontonummer: Yup.string().nullable(),
					swift: validateSwift(Yup.string()),
					landkode: requiredString,
					iban: Yup.string().nullable().optional(),
					bankkode: validateBankkode(Yup.string().nullable()),
					valuta: requiredString
						.nullable()
						.test('length', 'Valutakode kan kun bestå av tre store bokstaver', (val) =>
							/^[A-Z]{3}$/.test(val),
						),
					banknavn: Yup.string().nullable().optional(),
					bankAdresse1: Yup.string()
						.nullable()
						.optional()
						.test(
							'length',
							'Bankadresse må være mellom 1 og 34 tegn',
							(val) => !val || val.length <= 34,
						),
					bankAdresse2: Yup.string()
						.nullable()
						.optional()
						.test(
							'length',
							'Bankadresse må være mellom 1 og 34 tegn',
							(val) => !val || val.length <= 34,
						),
					bankAdresse3: Yup.string()
						.nullable()
						.optional()
						.test(
							'length',
							'Bankadresse må være mellom 1 og 34 tegn',
							(val) => !val || val.length <= 34,
						),
				}),
			),
			norskBankkonto: ifPresent(
				'$bankkonto.norskBankkonto',
				Yup.object().shape({
					kontonummer: validInputOrCheckboxTest(
						Yup.string().nullable(),
						'tilfeldigKontonummer',
						messages.required,
						null,
					),
					tilfeldigKontonummer: Yup.string().nullable(),
				}),
			),
		}),
	),
}
