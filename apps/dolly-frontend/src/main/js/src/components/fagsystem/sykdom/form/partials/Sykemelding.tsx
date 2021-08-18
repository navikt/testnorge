import React, { useState } from 'react'
import { get as _get } from 'lodash'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { SyntSykemelding } from './SyntSykemelding'
import { DetaljertSykemelding } from './DetaljertSykemelding'
import { SykemeldingForm, Diagnose } from '~/components/fagsystem/sykdom/SykemeldingTypes'

const initialValuesSyntSykemelding = {
	syntSykemelding: {
		startDato: new Date(),
		orgnummer: '',
		arbeidsforholdId: ''
	}
}

const initialValuesDetaljertSykemelding = {
	detaljertSykemelding: {
		arbeidsgiver: {
			navn: '',
			stillingsprosent: 100,
			yrkesbetegnelse: ''
		},
		biDiagnoser: [] as Array<Diagnose>,
		detaljer: {
			arbeidsforEtterEndtPeriode: false,
			beskrivHensynArbeidsplassen: '',
			tiltakArbeidsplass: '',
			tiltakNav: ''
		},
		hovedDiagnose: {
			diagnose: '',
			diagnosekode: '',
			system: ''
		},
		helsepersonell: {
			etternavn: '',
			fornavn: '',
			hprId: '',
			ident: '',
			mellomnavn: '',
			samhandlerType: ''
		},
		manglendeTilretteleggingPaaArbeidsplassen: false,
		mottaker: {
			navn: '',
			orgNr: '',
			adresse: {
				by: '',
				gate: '',
				land: '',
				postnummer: ''
			}
		},
		perioder: [
			{
				aktivitet: {
					aktivitet: null as string,
					behandlingsdager: 0,
					grad: 0,
					reisetilskudd: false
				},
				fom: '',
				tom: ''
			}
		],
		startDato: new Date(),
		umiddelbarBistand: false
	}
}

enum SykemeldingTyper {
	detaljert = 'DETALJERT',
	synt = 'SYNT'
}

export const Sykemelding = ({ formikBag }: SykemeldingForm) => {
	const [typeSykemelding, setTypeSykemelding] = useState(
		_get(formikBag.values, 'sykemelding').hasOwnProperty('detaljertSykemelding')
			? SykemeldingTyper.detaljert
			: SykemeldingTyper.synt
	)

	const handleToggleChange = (event: React.ChangeEvent<any>) => {
		setTypeSykemelding(event.target.value)
		if (event.target.value === SykemeldingTyper.detaljert) {
			formikBag.setFieldValue('sykemelding', initialValuesDetaljertSykemelding)
		} else formikBag.setFieldValue('sykemelding', initialValuesSyntSykemelding)
	}

	const toggleValues = [
		{
			value: SykemeldingTyper.synt,
			label: 'Generer sykemelding automatisk'
		},
		{
			value: SykemeldingTyper.detaljert,
			label: 'Lag detaljert sykemelding'
		}
	]

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name="sykemelding">
				{toggleValues.map(val => (
					<ToggleKnapp key={val.value} value={val.value} checked={typeSykemelding === val.value}>
						{val.label}
					</ToggleKnapp>
				))}
			</ToggleGruppe>
			{typeSykemelding === SykemeldingTyper.synt && <SyntSykemelding />}
			{typeSykemelding === SykemeldingTyper.detaljert && (
				<DetaljertSykemelding formikBag={formikBag} />
			)}
		</div>
	)
}
