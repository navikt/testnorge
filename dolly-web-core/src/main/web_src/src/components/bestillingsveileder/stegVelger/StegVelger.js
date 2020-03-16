import React, { useState, Fragment } from 'react'
import { Formik } from 'formik'
import Stegindikator from 'nav-frontend-stegindikator'
import { Navigation } from './Navigation/Navigation'
import { stateModifierFns } from '../stateModifier'
import { validate } from '~/utils/YupValidations'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'

import DisplayFormikState from '~/utils/DisplayFormikState'

import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/steg2/Steg2'
import { Steg3 } from './steg/steg3/Steg3'

const STEPS = [Steg1, Steg2, Steg3]

export const StegVelger = ({ initialValues, onSubmit, children }) => {
	const [step, setStep] = useState(0)

	const isLastStep = () => step === STEPS.length - 1
	const handleNext = () => setStep(step + 1)

	const handleBack = () => {
		if (step !== 0) setStep(step - 1)
	}

	const updateFromFullNames = (values, formikBag) =>{
		const namePaths = [`pdlforvalter.kontaktinformasjonForDoedsbo.adressat.navn`,
			`pdlforvalter.kontaktinformasjonForDoedsbo.adressat.kontaktperson`,
			`pdlforvalter.falskIdentitet.rettIdentitet.personnavn`]

		for(let i = 0; i < namePaths.length ; i++){
			const path = namePaths[i]
			const fullName = _get(values, `${path}.fulltNavn`)
			if (!_isNil(fullName)){
				const deltNavn = (fullName+'').split(" ")
				const mellomNavn = deltNavn.length===3 ? deltNavn[1] : ''

				formikBag.setFieldValue(`${path}`, {
					fornavn: deltNavn[0],
					mellomnavn: mellomNavn,
					etternavn: deltNavn[deltNavn.length-1]
				})
			}
		}
	}

	const _handleSubmit = (values, formikBag) => {
		const { setSubmitting } = formikBag

		if (!isLastStep()) {
			setSubmitting(false)
			handleNext()
			updateFromFullNames(values, formikBag)
			return
		}

		return onSubmit(values, formikBag)
	}

	const CurrentStepComponent = STEPS[step]

	const _validate = values => validate(values, CurrentStepComponent.validation)

	const labels = STEPS.map(v => ({ label: v.label }))

	return (
		<Formik initialValues={initialValues} validate={_validate} onSubmit={_handleSubmit}>
			{formikBag => {
				const stateModifier = stateModifierFns(formikBag.values, formikBag.setValues)
				return (
					<Fragment>
						<Stegindikator aktivtSteg={step} steg={labels} visLabel kompakt />

						<BestillingsveilederHeader />

						<CurrentStepComponent formikBag={formikBag} stateModifier={stateModifier} />

						 <DisplayFormikState {...formikBag} />

						<Navigation
							showPrevious={step > 0}
							onPrevious={handleBack}
							isLastStep={isLastStep()}
							formikBag={formikBag}
						/>
					</Fragment>
				)
			}}
		</Formik>
	)
}