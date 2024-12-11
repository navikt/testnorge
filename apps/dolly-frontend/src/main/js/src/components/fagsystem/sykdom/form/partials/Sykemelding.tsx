import React, { useState } from 'react'
import { SyntSykemelding } from './SyntSykemelding'
import { DetaljertSykemelding } from './DetaljertSykemelding'
import { Diagnose, SykemeldingForm } from '@/components/fagsystem/sykdom/SykemeldingTypes'
import { ToggleGroup } from '@navikt/ds-react'
import { addDays } from 'date-fns'
import { getRandomValue } from '@/components/fagsystem/utils'
import { SelectOptionsDiagnoser } from '@/components/fagsystem/sykdom/form/partials/SelectOptionsDiagnoser'

const randomDiagnose = getRandomValue(SelectOptionsDiagnoser())

const initialValuesSyntSykemelding = {
	syntSykemelding: {
		startDato: new Date(),
		orgnummer: '',
		arbeidsforholdId: '',
	},
}

const initialValuesDetaljertSykemelding = {
	detaljertSykemelding: {
		arbeidsgiver: {
			navn: '',
			stillingsprosent: 100,
			yrkesbetegnelse: '',
		},
		biDiagnoser: [] as Array<Diagnose>,
		detaljer: {
			arbeidsforEtterEndtPeriode: false,
			beskrivHensynArbeidsplassen: '',
			tiltakArbeidsplass: '',
			tiltakNav: '',
		},
		hovedDiagnose: {
			diagnose: randomDiagnose?.diagnoseNavn || '',
			diagnosekode: randomDiagnose?.value || '',
			system: randomDiagnose ? '2.16.578.1.12.4.1.1.7170' : '',
		},
		helsepersonell: {
			etternavn: '',
			fornavn: '',
			hprId: '',
			ident: '',
			mellomnavn: '',
			samhandlerType: '',
		},
		manglendeTilretteleggingPaaArbeidsplassen: false,
		mottaker: {
			navn: '',
			orgNr: null,
			adresse: {
				by: '',
				gate: '',
				land: '',
				postnummer: '',
			},
		},
		perioder: [
			{
				aktivitet: {
					aktivitet: null as unknown as string,
					behandlingsdager: 0,
					grad: 0,
					reisetilskudd: false,
				},
				fom: addDays(new Date(), -7),
				tom: addDays(new Date(), -1),
			},
		],
		kontaktMedPasient: {
			kontaktDato: null,
			begrunnelseIkkeKontakt: '',
		},
		startDato: new Date(),
		umiddelbarBistand: false,
	},
}

enum SykemeldingTyper {
	detaljert = 'DETALJERT',
	synt = 'SYNT',
}

export const Sykemelding = ({ formMethods }: SykemeldingForm) => {
	const [typeSykemelding, setTypeSykemelding] = useState(
		formMethods.watch('sykemelding').hasOwnProperty('detaljertSykemelding')
			? SykemeldingTyper.detaljert
			: SykemeldingTyper.synt,
	)

	const handleToggleChange = (value: SykemeldingTyper) => {
		setTypeSykemelding(value)
		if (value === SykemeldingTyper.detaljert) {
			formMethods.setValue('sykemelding', initialValuesDetaljertSykemelding)
		} else {
			formMethods.setValue('sykemelding', initialValuesSyntSykemelding)
		}
	}

	const toggleValues = [
		{
			value: SykemeldingTyper.synt,
			label: 'Generer sykemelding automatisk',
		},
		{
			value: SykemeldingTyper.detaljert,
			label: 'Lag detaljert sykemelding',
		},
	]

	return (
		<div className="toggle--wrapper">
			<ToggleGroup
				size={'small'}
				onChange={handleToggleChange}
				defaultValue={typeSykemelding}
				style={{ marginBottom: '5px', backgroundColor: '#ffffff' }}
			>
				{toggleValues.map((val) => (
					<ToggleGroup.Item key={val.value} value={val.value}>
						{val.label}
					</ToggleGroup.Item>
				))}
			</ToggleGroup>
			{typeSykemelding === SykemeldingTyper.synt && <SyntSykemelding />}
			{typeSykemelding === SykemeldingTyper.detaljert && (
				<DetaljertSykemelding formMethods={formMethods} />
			)}
		</div>
	)
}
