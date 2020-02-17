import React, { useState } from 'react'
import _get from 'lodash/get'
import Inntekt from './inntekt'
import { Formik } from 'formik'
// import NavButton from '~/components/ui/button/NavButton/NavButton'
import * as api from '../api'

const InntektStub = ({ formikBag, inntektPath }) => {
	// console.log('inntektPath :', inntektPath)
	const [fields, setFields] = useState({})
	// const [fields, setFields] = useState(_get(formikBag.values, `${inntektPath}`))

	console.log('fields :', fields)

	const initialValues = {
		inntektstype: '',
		inngaarIGrunnlagForTrekk: null,
		utloeserArbeidsgiveravgift: null,
		fordel: '', // kodeverk
		skatteOgAvgiftsregel: '', // kodeverk
		skattemessigBosattILand: '', // kodeverk
		opptjeningsland: '', // kodeverk
		beskrivelse: '', // flere kodeverk
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
				persontype: '' // kodeverk
			},
			spesielleInntjeningsforhold: {
				spesielleInntjeningsforhold: ''
			},
			utenlandskArtist: {}
		},
		antall: ''
	}

	return (
		<Formik
			initialValues={{}}
			// validateForm={validate}
			onSubmit={
				values =>
					api
						.validate(values)
						.then(response => setFields(response))
						.then(() => formikBag.setFieldValue(inntektPath, values))
				// .then(newResponse => console.log('values :', values))
			}
			// set formikBag ellernoe her ^. må på et vis få med path...
			render={({ handleSubmit }) => (
				<div>
					<Inntekt fields={fields} onValidate={handleSubmit} />
					{/* <NavButton type="hoved" onClick={values => validate(values)}>
						Opprett
					</NavButton> */}
				</div>
			)}
		/>
	)
}

export default InntektStub
