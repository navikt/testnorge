import { afterEach, describe, expect, it } from 'vitest'
import * as Yup from 'yup'
import { bankkontoValidation } from '@/components/fagsystem/bankkonto/form/validation'
import { landkoder, settKontoregisterLandkoder } from '@/service/services/kontoregister/landkoder'

const schema = Yup.object(bankkontoValidation)

const utenlandskBankkonto = (overrides: Record<string, string>) => ({
	bankkonto: {
		utenlandskBankkonto: {
			kontonummer: '',
			tilfeldigKontonummer: true,
			swift: '',
			landkode: 'AUS',
			bankkode: '',
			valuta: 'AUD',
			...overrides,
		},
	},
})

const valideringsfeil = async (values: object) => {
	try {
		await schema.validate(values, { abortEarly: false, context: {} })
		return []
	} catch (error) {
		return (error as Yup.ValidationError).inner.map((feil) => ({
			path: feil.path,
			message: feil.message,
		}))
	}
}

describe('bankkontoValidation for utenlandsk bankkonto', () => {
	afterEach(() => {
		settKontoregisterLandkoder(landkoder)
	})

	it('should use landkoder fetched from Kontoregister', async () => {
		settKontoregisterLandkoder([
			{
				landkode: 'DE',
				land: 'Tyskland',
				kreverIban: false,
				kreverBankkode: true,
				bankkodeLengde: 8,
				kreverBicOgBankkode: true,
			},
		])

		const feil = await valideringsfeil(
			utenlandskBankkonto({ landkode: 'DEU', swift: 'BANKDE22', valuta: 'EUR' }),
		)

		expect(feil).toContainEqual({
			path: 'bankkonto.utenlandskBankkonto.bankkode',
			message: 'Tyskland krever både BIC og bankkode',
		})
	})
	it('should require bankkode for Australia', async () => {
		const feil = await valideringsfeil(utenlandskBankkonto({ swift: 'BANKAU22' }))

		expect(feil).toContainEqual({
			path: 'bankkonto.utenlandskBankkonto.bankkode',
			message: 'Australia krever både BIC og bankkode',
		})
	})

	it('should require BIC for Australia', async () => {
		const feil = await valideringsfeil(utenlandskBankkonto({ bankkode: '123456' }))

		expect(feil).toContainEqual({
			path: 'bankkonto.utenlandskBankkonto.swift',
			message: 'Australia krever både BIC og bankkode',
		})
	})

	it('should accept Australia with BIC and bankkode', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({
				swift: 'BANKAU22',
				bankkode: '123456',
				banknavn: 'Banken',
				bankAdresse1: 'Bankveien 1',
			}),
		)

		expect(feil).toEqual([])
	})

	it('should reject bankkode with wrong length for Canada', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({
				landkode: 'CAN',
				swift: 'BANKCA22',
				bankkode: '123456',
				valuta: 'CAD',
			}),
		)

		expect(feil).toContainEqual({
			path: 'bankkonto.utenlandskBankkonto.bankkode',
			message: 'Bankkode for CA må være 9 tegn',
		})
	})

	it('should require landkode first in kontonummer for IBAN country', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({
				landkode: 'SWE',
				swift: 'BANKSE22',
				valuta: 'SEK',
				kontonummer: '1234567890123456789012',
			}),
		)

		expect(feil).toContainEqual({
			path: 'bankkonto.utenlandskBankkonto.kontonummer',
			message: 'Feil landkode i kontonummer. Den bør være SE (SE1234567890123456789012)',
		})
	})

	it('should require IBAN length for IBAN country', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({
				landkode: 'SWE',
				swift: 'BANKSE22',
				valuta: 'SEK',
				kontonummer: 'SE123',
			}),
		)

		expect(feil).toContainEqual({
			path: 'bankkonto.utenlandskBankkonto.kontonummer',
			message: 'Kontonummer for SE må være 24 tegn',
		})
	})

	it('should accept kontonummer without landkode for country without IBAN', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({
				landkode: 'USA',
				swift: 'BANKUS22',
				valuta: 'USD',
				kontonummer: '123456789',
			}),
		)

		expect(feil).toEqual([])
	})

	it('should treat whitespace-only bankkode as missing', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({ swift: 'BANKAU22', bankkode: '      ' }),
		)

		expect(feil).toContainEqual({
			path: 'bankkonto.utenlandskBankkonto.bankkode',
			message: 'Australia krever både BIC og bankkode',
		})
	})

	it('should accept missing bankkode for country with optional bankkode', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({ landkode: 'USA', swift: 'BANKUS22', valuta: 'USD' }),
		)

		expect(feil).toEqual([])
	})

	it('should require banknavn and adresselinje 1 when bankkode is used', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({ swift: 'BANKAU22', bankkode: '123456' }),
		)

		expect(feil).toEqual([
			{
				path: 'bankkonto.utenlandskBankkonto.banknavn',
				message: 'Banknavn er påkrevd ved bruk av bankkode',
			},
			{
				path: 'bankkonto.utenlandskBankkonto.bankAdresse1',
				message: 'Adresselinje 1 er påkrevd ved bruk av bankkode',
			},
		])
	})

	it('should not require banknavn and adresselinje 1 without bankkode', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({ landkode: 'USA', swift: 'BANKUS22', valuta: 'USD' }),
		)

		expect(feil).toEqual([])
	})

	it('should accept missing bankkode for country without bankkode', async () => {
		const feil = await valideringsfeil(
			utenlandskBankkonto({ landkode: 'DEU', swift: 'BANKDE22', valuta: 'EUR' }),
		)

		expect(feil).toEqual([])
	})
})
