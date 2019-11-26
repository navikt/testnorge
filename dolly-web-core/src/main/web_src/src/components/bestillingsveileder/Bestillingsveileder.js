import React, { useState } from 'react'
import { getInitialValues } from '~/service/attributter/Attributter'
import { StegVelger } from './StegVelger'
import { Steg1 } from './steg/Steg1'
import { Steg2 } from './steg/Steg2'
import { Steg3 } from './steg/Steg3'
import { mergeKeepShape } from '~/utils/Merge'

import './bestillingsveileder.less'

const steps = [Steg1, Steg2, Steg3]

export const Bestillingsveileder = props => {
	const [attributter, setAttributter] = useState(getInitialValues())
	const [savedValues, setSavedValues] = useState({})

	const checkAttributter = attrs => setAttributter(Object.assign({}, attributter, attrs))

	const handleSubmit = (values, formikBag) => {
		props.createBestillingMal(values.malNavn) //Nå sjekkes ikke malnavn
		props.sendBestilling(values)
	}

	const initialValuesSteps = steps.reduce(
		(acc, curr) => Object.assign({}, acc, curr.initialValues(attributter)),
		{}
	)

	// Merge with savedValues
	const initialValues = mergeKeepShape(initialValuesSteps, savedValues)

	return (
		<div className="bestillingsveileder">
			<StegVelger
				steps={steps}
				initialValues={initialValues}
				copyValues={setSavedValues}
				onSubmit={handleSubmit}
			>
				{(CurrentStep, formikBag) => (
					<CurrentStep
						formikBag={formikBag}
						attributter={attributter}
						checkAttributter={checkAttributter}
						props={props}
					/>
				)}
			</StegVelger>
		</div>
	)
}

// Hvis vi skal sjekke om et malnavn er brukt før
// const _submit = values => {
// const { maler } = this.props
// if (values.malNavn && values.malNavn !== '') {
//     this.setState({ showMalNavnError: false })
//     maler.forEach(mal => {
//         if (mal.malBestillingNavn === values.malNavn) {
//             this.setState({ showMalNavnError: true })
//         }
//     })
// }
