import React, { useEffect, useState } from 'react'
import Inntekt from '@/components/inntektStub/validerInntekt/Inntekt'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'
import _ from 'lodash'
import { Form, useFormContext, useWatch } from 'react-hook-form'
import _get from 'lodash/get'

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
	const formMethods = useFormContext()
	const [fields, setFields] = useState({})
	const watched = useWatch()
	const inntektValues = _get(watched, inntektPath)
	const {
		beloep,
		startOpptjeningsperiode,
		sluttOpptjeningsperiode,
		inntektstype,
		tilleggsinformasjonstype,
	} = inntektValues

	useEffect(() => {
		if (!_.isEmpty(inntektstype)) {
			InntektstubService.validate(_.omitBy(inntektValues, (value) => value === '' || !value)).then(
				(response) => {
					setFields(response)
				},
			)
		}
		formMethods.trigger(`${inntektPath}.inntektstype`)
	}, [inntektValues])

	useEffect(() => {
		Object.entries(fields).forEach((entry) => {
			const fieldName = entry[0]
			const fieldState = formMethods.getFieldState(`${inntektPath}.${fieldName}`)
			fieldState.invalid &&
				!fieldState.isDirty &&
				formMethods.getValues(`${inntektPath}.${fieldName}`) !== null &&
				formMethods.setValue(`${inntektPath}.${fieldName}`, null)
			removeEmptyFieldsFromForm(entry)
		})
		if (
			!tilleggsinformasjonstype &&
			formMethods.getValues(`${inntektPath}.tilleggsinformasjon`) !== undefined
		) {
			formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, undefined)
		}
	}, [fields])

	useEffect(() => {
		if (!tilleggsinformasjonstype) {
			return
		}
		formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, {
			[`${tilleggsinformasjonAttributter[tilleggsinformasjonstype]}`]: {},
		})
	}, [tilleggsinformasjonstype])

	const setForm = (values) => {
		const nullstiltInntekt = {
			beloep: beloep,
			startOpptjeningsperiode: startOpptjeningsperiode,
			sluttOpptjeningsperiode: sluttOpptjeningsperiode,
			inntektstype: inntektstype,
		}

		if (values.inntektstype !== inntektstype) {
			formMethods.setValue(inntektPath, nullstiltInntekt)
		} else {
			formMethods.setValue(inntektPath, {
				...inntektValues,
				...values,
			})
		}
	}

	const removeEmptyFieldsFromForm = (entry) => {
		const name = entry[0]
		const valueArray = entry[1]
		if (
			valueArray.length === 1 &&
			valueArray[0] === '<TOM>' &&
			formMethods.getValues(`${inntektPath}.${name}`) !== undefined
		) {
			formMethods.setValue(`${inntektPath}.${name}`, undefined)
			formMethods.clearErrors(`${inntektPath}.${name}`)
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
				if (inntektstype && values.inntektstype !== inntektstype) {
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
					onValidate={() => formMethods.trigger('inntekt')}
					formMethods={formMethods}
					path={inntektPath}
				/>
			</div>
		</Form>
	)
}

export default InntektStub
