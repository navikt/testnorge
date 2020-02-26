import React, { useState } from 'react'
import _get from 'lodash/get'
import Inntekt from './inntekt'
import { Formik } from 'formik'
import * as api from '../api'

const InntektStub = ({ formikBag, inntektPath }) => {
	const [fields, setFields] = useState({})
	// const [inntektstype, setInntektstype] = useState(
	// 	_get(formikBag.values, `${inntektPath}.inntektstype`)
	// )

	const initialValues = {
		inntektstype: '',
		inngaarIGrunnlagForTrekk: null,
		utloeserArbeidsgiveravgift: null,
		fordel: '',
		skatteOgAvgiftsregel: '',
		skattemessigBosattILand: '',
		opptjeningsland: '',
		beskrivelse: '',
		tilleggsinformasjon: {
			bilOgBaat: {},
			bonusFraForsvaret: {
				aaretUtbetalingenGjelderFor: ''
			},
			dagmammaIEgenBolig: {},
			etterbetalingsperiode: {
				startdato: null,
				sluttdato: null
			},
			inntektPaaNorskKontinentalsokkel: {},
			livrente: {},
			lottOgPart: {},
			nettoloenn: {},
			pensjon: {
				grunnpensjonsbeloep: '',
				heravEtterlattepensjon: '',
				pensjonsgrad: '',
				tidsrom: {
					startdato: null,
					sluttdato: null
				},
				tilleggspensjonsbeloep: '',
				ufoeregrad: ''
			},
			reiseKostOgLosji: {
				persontype: ''
			},
			inntjeningsforhold: {
				inntjeningsforhold: ''
			},
			utenlandskArtist: {}
		},
		antall: ''
	}

	const setFormikBag = values => {
		const tilleggsinformasjonPaths = {
			aaretUtbetalingenGjelderFor: 'bonusFraForsvaret.aaretUtbetalingenGjelderFor', // Lønnsinntekt
			etterbetalingsperiodeStart: 'etterbetalingsperiode.startdato', // Ytelse fra offentlige + Pensjon eller trygd
			etterbetalingsperiodeSlutt: 'etterbetalingsperiode.sluttdato', // Ytelse fra offentlige + Pensjon eller trygd
			grunnpensjonsbeloep: 'pensjon.grunnpensjonsbeloep', // Ytelse fra offentlige + Pensjon eller trygd
			heravEtterlattepensjon: 'pensjon.heravEtterlattepensjon', // Ytelse fra offentlige + Pensjon eller trygd
			pensjonsgrad: 'pensjon.pensjonsgrad', // Ytelse fra offentlige + Pensjon eller trygd
			pensjonTidsromStart: 'pensjon.tidsrom.startdato', // Ytelse fra offentlige + Pensjon eller trygd
			pensjonTidsromSlutt: 'pensjon.tidsrom.sluttdato', // Ytelse fra offentlige + Pensjon eller trygd
			tilleggspensjonsbeloep: 'pensjon.tilleggspensjonsbeloep', // Ytelse fra offentlige + Pensjon eller trygd
			ufoeregrad: 'pensjon.ufoeregrad', // Ytelse fra offentlige + Pensjon eller trygd
			persontype: 'reiseKostOgLosji.persontype', // Lønnsinntekt
			inntjeningsforhold: 'inntjeningsforhold.inntjeningsforhold' // Lønnsinntekt
		}
		const tilleggsinformasjonAttributter = {
			BilOgBaat: 'bilOgBaat', // Lønnsinntekt
			DagmammaIEgenBolig: 'dagmammaIEgenBolig', // Næringsinntekt
			NorskKontinentalsokkel: 'inntektPaaNorskKontinentalsokkel', // Lønnsinntekt
			Livrente: 'livrente', // Pensjon eller trygd
			LottOgPartInnenFiske: 'lottOgPart', // Næringsinntekt
			Nettoloennsordning: 'nettoloenn', // Lønnsinntekt
			UtenlandskArtist: 'utenlandskArtist' // Lønnsinntekt
		}
		for (var [key, value] of Object.entries(values)) {
			tilleggsinformasjonAttributter[value]
				? formikBag.setFieldValue(
						`${inntektPath}.tilleggsinformasjon.${tilleggsinformasjonAttributter[value]}`,
						{}
				  )
				: tilleggsinformasjonPaths[key]
				? formikBag.setFieldValue(
						`${inntektPath}.tilleggsinformasjon.${tilleggsinformasjonPaths[key]}`,
						value
				  )
				: formikBag.setFieldValue(`${inntektPath}.${key}`, value)
		}
	}

	return (
		<Formik
			initialValues={{}}
			onSubmit={
				values =>
					api
						.validate(values)
						.then(response => setFields(response))
						.then(() => setFormikBag(values))
				// .then(() => setInntektstype(values.inntektstype))
			}
			render={({ handleSubmit }) => (
				<div>
					<Inntekt fields={fields} onValidate={handleSubmit} />
				</div>
			)}
		/>
	)
}

export default InntektStub
