import { describe, expect, it } from 'vitest'
import { fireEvent, render, screen } from '@testing-library/react'
import React from 'react'
import {
	erHistoriskOpplysning,
	PdlHistorikkFieldArray,
} from '@/components/ui/historikk/PdlHistorikkFieldArray'
import {
	beregnHistorikkStatus,
	getEksisterendeSomErstattes,
	getHistorikkTag,
	historikkRegler,
} from '@/components/fagsystem/pdlf/form/partials/historikk/HistorikkRekkefoelge'

type TestOpplysning = {
	hendelseId?: string
	gradering: string
	metadata?: { historisk?: boolean; opplysningsId?: string }
}

const pdlfData: Array<TestOpplysning> = [
	{ hendelseId: 'h-2', gradering: 'UGRADERT' },
	{ hendelseId: 'h-1', gradering: 'FORTROLIG' },
]

const pdlData = [
	{ metadata: { opplysningsId: 'h-2', historisk: false } },
	{ metadata: { opplysningsId: 'h-1', historisk: true } },
]

const renderOpplysning = (element: TestOpplysning, idx: number) => (
	<span>{`${element.gradering}-${idx}`}</span>
)

describe('erHistoriskOpplysning', () => {
	it('should use metadata.historisk when present on the element', () => {
		expect(erHistoriskOpplysning({ metadata: { historisk: true } })).toBe(true)
		expect(erHistoriskOpplysning({ metadata: { historisk: false } }, pdlData)).toBe(false)
	})

	it('should look up PDL metadata through hendelseId', () => {
		expect(erHistoriskOpplysning({ hendelseId: 'h-1' }, pdlData)).toBe(true)
		expect(erHistoriskOpplysning({ hendelseId: 'h-2' }, pdlData)).toBe(false)
	})

	it('should treat elements not found in PDL as gjeldende', () => {
		expect(erHistoriskOpplysning({ hendelseId: 'ukjent' }, pdlData)).toBe(false)
		expect(erHistoriskOpplysning({}, pdlData)).toBe(false)
		expect(erHistoriskOpplysning({ hendelseId: 'h-1' })).toBe(false)
	})
})

describe('PdlHistorikkFieldArray', () => {
	it('should hide historiske opplysninger in a collapsed panel and keep original index', () => {
		render(
			<PdlHistorikkFieldArray data={pdlfData} pdlData={pdlData}>
				{renderOpplysning}
			</PdlHistorikkFieldArray>,
		)

		expect(screen.getByText('UGRADERT-0')).toBeInTheDocument()
		expect(screen.queryByText('FORTROLIG-1')).not.toBeInTheDocument()

		fireEvent.click(screen.getByText('Historikk'))

		expect(screen.getByText('FORTROLIG-1')).toBeInTheDocument()
	})

	it('should not render a historikk panel when nothing is historisk', () => {
		render(<PdlHistorikkFieldArray data={pdlfData}>{renderOpplysning}</PdlHistorikkFieldArray>)

		expect(screen.getByText('UGRADERT-0')).toBeInTheDocument()
		expect(screen.getByText('FORTROLIG-1')).toBeInTheDocument()
		expect(screen.queryByText('Historikk')).not.toBeInTheDocument()
	})
})

const iFjor = new Date(new Date().getFullYear() - 1, 0, 1).toISOString()
const toAarSiden = new Date(new Date().getFullYear() - 2, 0, 1).toISOString()
const nesteAar = new Date(new Date().getFullYear() + 1, 0, 1).toISOString()

describe('beregnHistorikkStatus', () => {
	it('should keep the newest adressebeskyttelse gjeldende per master', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG' }, { master: 'PDL' }, { master: 'FREG' }, { master: 'PDL' }],
				historikkRegler.adressebeskyttelse,
			),
		).toEqual(['ERSTATTET', 'ERSTATTET', 'GJELDENDE', 'GJELDENDE'])
	})

	it('should keep only the newest bostedsadresse gjeldende, regardless of master', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG' }, { master: 'PDL' }],
				historikkRegler.bostedsadresse,
			),
		).toEqual(['ERSTATTET', 'GJELDENDE'])
	})

	it('should keep the newest kontaktadresse gjeldende per master', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG' }, { master: 'PDL' }, { master: 'FREG' }],
				historikkRegler.kontaktadresse,
			),
		).toEqual(['ERSTATTET', 'GJELDENDE', 'GJELDENDE'])
	})

	it('should mark the newest address as historisk when gyldig t.o.m. has passed', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG' }, { master: 'FREG', gyldigTilOgMed: iFjor }],
				historikkRegler.bostedsadresse,
			),
		).toEqual(['ERSTATTET', 'UTLOEPT'])
		expect(
			beregnHistorikkStatus([{ gyldigTilOgMed: nesteAar }], historikkRegler.oppholdsadresse),
		).toEqual(['GJELDENDE'])
	})

	it('should give PDL kontaktadresse without t.o.m. one year from f.o.m.', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'PDL', gyldigFraOgMed: toAarSiden }],
				historikkRegler.kontaktadresse,
			),
		).toEqual(['UTLOEPT'])
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG', gyldigFraOgMed: toAarSiden }],
				historikkRegler.kontaktadresse,
			),
		).toEqual(['GJELDENDE'])
	})
})

describe('beregnHistorikkStatus with backend adjustments', () => {
	it('should group on forced master PDL for NPID and Test-Norge persons', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG' }, { master: 'PDL' }],
				historikkRegler.kontaktadresse,
				[],
				{ tvungenMaster: 'PDL' },
			),
		).toEqual(['ERSTATTET', 'GJELDENDE'])
	})

	it('should keep PDL kontaktadresse without f.o.m. gjeldende when t.o.m. has passed', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'PDL', gyldigTilOgMed: iFjor }],
				historikkRegler.kontaktadresse,
			),
		).toEqual(['GJELDENDE'])
	})

	it('should ignore t.o.m. that is not after the f.o.m. derived from the older address', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG', gyldigTilOgMed: toAarSiden }],
				historikkRegler.bostedsadresse,
				[{ master: 'FREG', gyldigFraOgMed: toAarSiden, gyldigTilOgMed: iFjor }],
			),
		).toEqual(['GJELDENDE'])
	})

	it('should not inherit f.o.m. from existing oppholdsadresser', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG', gyldigTilOgMed: toAarSiden }],
				historikkRegler.oppholdsadresse,
				[{ master: 'FREG', gyldigFraOgMed: toAarSiden, gyldigTilOgMed: iFjor }],
			),
		).toEqual(['UTLOEPT'])
	})

	it('should derive f.o.m. from the generated t.o.m. of an older PDL kontaktadresse', () => {
		expect(
			beregnHistorikkStatus(
				[
					{ master: 'PDL', gyldigFraOgMed: toAarSiden },
					{ master: 'FREG', gyldigTilOgMed: toAarSiden },
				],
				historikkRegler.kontaktadresse,
			),
		).toEqual(['UTLOEPT', 'GJELDENDE'])
	})

	it('should not generate t.o.m. on an existing PDL kontaktadresse', () => {
		expect(
			beregnHistorikkStatus(
				[{ master: 'FREG', gyldigTilOgMed: iFjor }],
				historikkRegler.kontaktadresse,
				[{ master: 'PDL', gyldigFraOgMed: toAarSiden }],
			),
		).toEqual(['UTLOEPT'])
	})

	it('should treat bostedsadresse without adressetype as norsk only for FNR', () => {
		const kontekst = { innflytting: { innflyttingsdato: null } }
		expect(
			beregnHistorikkStatus([{}], historikkRegler.bostedsadresse, [], {
				...kontekst,
				identtype: 'DNR',
			}),
		).toEqual(['ERSTATTET'])
		expect(
			beregnHistorikkStatus([{}], historikkRegler.bostedsadresse, [], {
				...kontekst,
				identtype: 'FNR',
			}),
		).toEqual(['GJELDENDE'])
	})

	it('should end the newest norsk bostedsadresse on utflytting', () => {
		const kontekst = { utflytting: { utflyttingsdato: null } }
		expect(
			beregnHistorikkStatus(
				[{ vegadresse: {} }, { vegadresse: {} }],
				historikkRegler.bostedsadresse,
				[],
				kontekst,
			),
		).toEqual(['ERSTATTET', 'UTLOEPT'])
		expect(beregnHistorikkStatus([{}], historikkRegler.bostedsadresse, [], kontekst)).toEqual([
			'GJELDENDE',
		])
		expect(
			beregnHistorikkStatus(
				[{ utenlandskAdresse: {} }],
				historikkRegler.bostedsadresse,
				[],
				kontekst,
			),
		).toEqual(['GJELDENDE'])
		expect(beregnHistorikkStatus([{}], historikkRegler.oppholdsadresse, [], kontekst)).toEqual([
			'GJELDENDE',
		])
	})

	it('should replace bostedsadresse when innflytting adds a norsk address', () => {
		const kontekst = { innflytting: { innflyttingsdato: null } }
		expect(
			beregnHistorikkStatus(
				[{ utenlandskAdresse: {} }],
				historikkRegler.bostedsadresse,
				[],
				kontekst,
			),
		).toEqual(['ERSTATTET'])
		expect(beregnHistorikkStatus([{}], historikkRegler.bostedsadresse, [], kontekst)).toEqual([
			'GJELDENDE',
		])
	})
})

describe('getEksisterendeSomErstattes', () => {
	it('should skip existing entries that are already historisk', () => {
		const regel = historikkRegler.kontaktadresse
		expect(
			getEksisterendeSomErstattes(
				[{ master: 'FREG' }],
				[{ master: 'FREG', gyldigTilOgMed: toAarSiden }],
				regel,
			),
		).toHaveLength(0)
		expect(
			getEksisterendeSomErstattes(
				[{ master: 'FREG' }],
				[{ master: 'FREG', hendelseId: 'a' }],
				regel,
				{
					pdlEksisterende: [{ metadata: { historisk: true, opplysningsId: 'a' } }],
				},
			),
		).toHaveLength(0)
		expect(
			getHistorikkTag(regel, [{ master: 'FREG', gyldigTilOgMed: toAarSiden }])(0, [
				{ master: 'FREG' },
			]),
		).toBeNull()
	})

	it('should only replace existing entries with the same master for per-master types', () => {
		const eksisterende = [{ master: 'FREG' }, { master: 'PDL' }]
		expect(
			getEksisterendeSomErstattes(
				[{ master: 'PDL' }],
				eksisterende,
				historikkRegler.oppholdsadresse,
			),
		).toEqual([{ master: 'PDL' }])
		expect(
			getEksisterendeSomErstattes(
				[{ master: 'PDL' }],
				eksisterende,
				historikkRegler.bostedsadresse,
			),
		).toHaveLength(2)
	})
})

describe('getHistorikkTag', () => {
	it('should not tag entries when nothing becomes historisk', () => {
		const tag = getHistorikkTag(historikkRegler.kontaktadresse)
		expect(tag(0, [{ master: 'FREG' }, { master: 'PDL' }])).toBeNull()
	})

	it('should show master on gjeldende tag for per-master types', () => {
		const tag = getHistorikkTag(historikkRegler.kontaktadresse)
		const values = [{ master: 'FREG' }, { master: 'FREG' }]

		render(
			<>
				{tag(0, values)}
				{tag(1, values)}
			</>,
		)

		expect(screen.getByText('Blir historisk')).toBeInTheDocument()
		expect(screen.getByText('Blir gjeldende for FREG')).toBeInTheDocument()
	})

	it('should mark a single new entry as gjeldende when it replaces existing data', () => {
		render(<>{getHistorikkTag(historikkRegler.bostedsadresse, [{}])(0, [{}])}</>)

		expect(screen.getByText('Blir gjeldende')).toBeInTheDocument()
	})
})
