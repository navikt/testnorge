import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'

const getInitialInntekt = () => {
	return null
}

export const PensjonsgivendeInntektForm = ({ path, formikBag }) => {
	return (
		<FormikDollyFieldArray name={path} header="Inntekter" newEntry={getInitialInntekt} nested>
			{(path, idx) => {
				return (
					<React.Fragment key={idx}>
						<h4>Test</h4>
					</React.Fragment>
				)
			}}
		</FormikDollyFieldArray>
	)
}
