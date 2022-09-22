import React, { useState } from 'react'
import { get as _get } from 'lodash'
import { SyntSykemelding } from './SyntSykemelding'
import { DetaljertSykemelding } from './DetaljertSykemelding'
import { Diagnose, SykemeldingForm } from '~/components/fagsystem/sykdom/SykemeldingTypes'
import { ToggleGroup } from '@navikt/ds-react'

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
			diagnose: '',
			diagnosekode: '',
			system: '',
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
			orgNr: '',
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
					aktivitet: null as string,
					behandlingsdager: 0,
					grad: 0,
					reisetilskudd: false,
				},
				fom: '',
				tom: '',
			},
		],
		startDato: new Date(),
		umiddelbarBistand: false,
	},
}

enum SykemeldingTyper {
	detaljert = 'DETALJERT',
	synt = 'SYNT',
}

export const Sykemelding = ({ formikBag }: SykemeldingForm) => {
	const [typeSykemelding, setTypeSykemelding] = useState(
		_get(formikBag.values, 'sykemelding').hasOwnProperty('detaljertSykemelding')
			? SykemeldingTyper.detaljert
			: SykemeldingTyper.synt
	)

	const handleToggleChange = (value: SykemeldingTyper) => {
		setTypeSykemelding(value)
		if (value === SykemeldingTyper.detaljert) {
			formikBag.setFieldValue('sykemelding', initialValuesDetaljertSykemelding)
		} else {
			formikBag.setFieldValue('sykemelding', initialValuesSyntSykemelding)
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
				defaultValue={SykemeldingTyper.synt}
				style={{ marginBottom: '5px' }}
			>
				{toggleValues.map((val) => (
					<ToggleGroup.Item key={val.value} value={val.value}>
						{val.label}
					</ToggleGroup.Item>
				))}
			</ToggleGroup>
			{typeSykemelding === SykemeldingTyper.synt && <SyntSykemelding />}
			{typeSykemelding === SykemeldingTyper.detaljert && (
				<DetaljertSykemelding formikBag={formikBag} />
			)}
		</div>
	)
}
