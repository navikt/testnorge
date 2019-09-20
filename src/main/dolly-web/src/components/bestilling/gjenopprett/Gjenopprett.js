import React, { Fragment } from 'react'
import * as yup from 'yup'
import { Formik, FieldArray } from 'formik'
import Knapp from 'nav-frontend-knapper'
import DollyModal from '~/components/ui/modal/DollyModal'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'
import Formatters from '~/utils/DataFormatter'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

export default function GjenopprettBestilling(props) {
	const { bestilling, closeModal } = props

	const submitFormik = async values => {
		const envsQuery = Formatters.arrayToString(values.environments)
			.replace(/ /g, '')
			.toLowerCase()
		await props.gjenopprettBestilling(envsQuery)
		await props.getBestillinger()
	}

	const schemaValidation = yup.object().shape({
		environments: yup.array().required('Velg minst ett miljø')
	})

	const { environments } = bestilling

	return (
		<DollyModal isOpen={true} closeModal={closeModal}>
			<div style={{ paddingLeft: 20, paddingRight: 20 }}>
				<h1>Bestilling #{bestilling.id}</h1>
				<StaticValue header="Bestilt miljø" value={Formatters.arrayToString(environments)} />
				<br />
				<hr />
			</div>
			<Formik
				initialValues={{
					environments
				}}
				onSubmit={submitFormik}
				validationSchema={schemaValidation}
				render={formikProps => {
					return (
						<Fragment>
							<FieldArray
								name="environments"
								render={arrayHelpers => (
									<MiljoVelgerConnector
										heading={'Velg miljø å gjenopprette i'}
										arrayHelpers={arrayHelpers}
										arrayValues={formikProps.values.environments}
									/>
								)}
							/>
							<div className="dollymodal_buttons">
								<Knapp autoFocus type="standard" onClick={closeModal}>
									Avbryt
								</Knapp>
								<Knapp type="hoved" onClick={formikProps.submitForm}>
									Utfør
								</Knapp>
							</div>
						</Fragment>
					)
				}}
			/>
		</DollyModal>
	)
}
