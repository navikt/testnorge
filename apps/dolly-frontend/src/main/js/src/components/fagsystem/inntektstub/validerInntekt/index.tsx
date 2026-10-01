import React, { useEffect, useRef, useState } from 'react'
import Inntekt from '@/components/fagsystem/inntektstub/validerInntekt/Inntekt'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'
import * as _ from 'lodash-es'
import { Form, useFormContext, useWatch } from 'react-hook-form'

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
	const [isLoadingFields, setIsLoadingFields] = useState(false)
	const sisteFieldsRequestId = useRef(0)
	const inntektValues = useWatch({ name: inntektPath })
	const {
		beloep,
		startOpptjeningsperiode,
		sluttOpptjeningsperiode,
		inntektstype,
		tilleggsinformasjonstype,
		tilleggsinformasjon,
	} = inntektValues
	const inntektValuesJson = JSON.stringify(inntektValues)

	useEffect(() => {
		formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, undefined)
	}, [inntektstype])

	const getFields = (values) => {
		const requestId = ++sisteFieldsRequestId.current
		setIsLoadingFields(true)
		InntektstubService.validate(_.omitBy(values, (value) => value === '' || !value))
			.then((response) => {
				if (requestId === sisteFieldsRequestId.current) {
					setFields(response)
				}
			})
			.finally(() => {
				if (requestId === sisteFieldsRequestId.current) {
					setIsLoadingFields(false)
				}
			})
	}

	useEffect(() => {
		if (!_.isEmpty(inntektstype)) {
			getFields(inntektValues)
		}
		formMethods.trigger(inntektPath)
	}, [inntektValuesJson])

	useEffect(() => {
		Object.entries(fields).forEach((entry) => {
			const fieldName = entry[0]
			const fieldState = formMethods.getFieldState(`${inntektPath}.${fieldName}`)
			if (
				fieldState.invalid &&
				!fieldState.isDirty &&
				formMethods.getValues(`${inntektPath}.${fieldName}`) !== null
			) {
				formMethods.setValue(`${inntektPath}.${fieldName}`, null)
			}
			removeEmptyFieldsFromForm(entry)
		})
	}, [fields])

	useEffect(() => {
		if (!tilleggsinformasjonstype) {
			clearTilleggsinformasjon()
		} else
			formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, {
				[`${tilleggsinformasjonAttributter[tilleggsinformasjonstype]}`]: {},
			})
	}, [tilleggsinformasjonstype])

	useEffect(() => {
		if (!tilleggsinformasjonstype) {
			clearTilleggsinformasjon()
		}
	}, [tilleggsinformasjon])

	const clearTilleggsinformasjon = () => {
		formMethods.setValue(`${inntektPath}.tilleggsinformasjon`, undefined)
		formMethods.clearErrors(`manual.${inntektPath}.tilleggsinformasjon`)
		formMethods.clearErrors(`${inntektPath}.tilleggsinformasjon`)
	}

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
			formMethods.clearErrors(`manual.${inntektPath}.${name}`)
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
			style={{ display: 'contents' }}
			onSubmit={(values: any) => {
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
				getFields(values)
				clearEmptyValuesAndFields(values)
				setForm(values)
			}}
		>
			<div style={{ display: 'contents' }}>
				<Inntekt
					fields={fields}
					isLoadingFields={isLoadingFields}
					onValidate={() => formMethods.trigger('inntekt')}
					formMethods={formMethods}
					path={inntektPath}
				/>
			</div>
		</Form>
	)
}

export default InntektStub
