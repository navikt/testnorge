import React, { useEffect, useState } from 'react'
import Inntekt from '@/components/inntektStub/validerInntekt/Inntekt'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'
import * as _ from 'lodash-es'
import { Form, useForm } from 'react-hook-form'

const tilleggsinformasjonAttributter = {
	BilOgBaat: 'bilOgBaat',
	DagmammaIEgenBolig: 'dagmammaIEgenBolig',
	NorskKontinentalsokkel: 'inntektPaaNorskKontinentalsokkel',
	Livrente: 'livrente',
	LottOgPartInnenFiske: 'lottOgPart',
	Nettoloennsordning: 'nettoloenn',
	UtenlandskArtist: 'utenlandskArtist',
	BonusFraForsvaret: 'bonusFraForsvaret',
	ReiseKostOgLosji: 'reiseKostOgLosji',
}

const InntektStub = ({ inntektPath }) => {
	const formMethods = useForm({
		defaultValues: {},
	})
	const [fields, setFields] = useState({})
	const [inntektValues] = useState(_.get(formMethods.getValues(), inntektPath))
	const [currentInntektstype, setCurrentInntektstype] = useState(
		_.get(formMethods.getValues(), `${inntektPath}.inntektstype`),
	)
	const currentTilleggsinformasjonstype = _.get(
		formMethods.getValues(),
		`${inntektPath}.tilleggsinformasjonstype`,
	)

	useEffect(() => {
		setCurrentInntektstype(_.get(formMethods.getValues(), `${inntektPath}.inntektstype`))
	}, [formMethods.getValues()])

	useEffect(() => {
		if (
			inntektValues.inntektstype &&
			inntektValues.inntektstype !== '' &&
			Object.keys(fields).length < 1
		) {
			InntektstubService.validate(_.omitBy(inntektValues, (value) => value === '' || !value)).then(
				(response) => setFields(response),
			)
		}
	}, [inntektValues, fields])

	useEffect(() => {
		Object.entries(fields).forEach((entry) => {
			removeEmptyFieldsFromFormik(entry)
		})
	}, [fields])

	useEffect(() => {
		if (!currentTilleggsinformasjonstype) {
			formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, undefined)
			return
		}
		formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, {
			[`${tilleggsinformasjonAttributter[currentTilleggsinformasjonstype]}`]: {},
		})
	}, [currentTilleggsinformasjonstype])

	const setForm = (values) => {
		const nullstiltInntekt = {
			beloep: _.get(formMethods.getValues(), `${inntektPath}.beloep`),
			startOpptjeningsperiode: _.get(
				formMethods.getValues(),
				`${inntektPath}.startOpptjeningsperiode`,
			),
			sluttOpptjeningsperiode: _.get(
				formMethods.getValues(),
				`${inntektPath}.sluttOpptjeningsperiode`,
			),
			inntektstype: values.inntektstype,
		}

		if (values.inntektstype !== currentInntektstype) {
			formMethods.setValue(inntektPath, nullstiltInntekt)
		} else {
			formMethods.setValue(inntektPath, {
				..._.get(formMethods.getValues(), inntektPath),
				...values,
			})
		}
	}

	const removeEmptyFieldsFromFormik = (entry) => {
		const name = entry[0]
		const valueArray = entry[1]
		if (
			valueArray.length === 1 &&
			valueArray[0] === '<TOM>' &&
			_.get(formMethods.getValues(), `${inntektPath}.${name}`)
		) {
			formMethods.setValue(`${inntektPath}.${name}`, undefined)
		}
	}

	const clearEmptyValuesAndFields = (values) => {
		for (const [key, value] of Object.entries(fields)) {
			if (values[key] === undefined && value.length !== 1) {
				values[key] = null
			}
		}

		for (const [key, value] of Object.entries(values)) {
			if (value === '' || value === '<TOM>') {
				values[key] = undefined
			}
		}
	}

	return (
		<Form
			onSubmit={(values) => {
				if (currentInntektstype && values.inntektstype !== currentInntektstype) {
					values = { inntektstype: values.inntektstype }
				}
				const emptyableFields = Object.entries(fields).filter(
					(field) => field?.[1]?.[0] === '<TOM>' && field?.[1]?.length > 2,
				)
				for (const [key] of emptyableFields) {
					if (!values[key] && key !== 'tilleggsinformasjonstype') {
						values[key] = '<TOM>'
					}
				}
				InntektstubService.validate(_.omitBy(values, (value) => value === '' || !value)).then(
					(response) => setFields(response),
				)
				clearEmptyValuesAndFields(values)
				setForm(values)
			}}
		>
			<div>
				<Inntekt
					fields={fields}
					onValidate={formMethods.handleSubmit()}
					formMethods={formMethods}
					path={inntektPath}
				/>
			</div>
		</Form>
	)
}

export default InntektStub
