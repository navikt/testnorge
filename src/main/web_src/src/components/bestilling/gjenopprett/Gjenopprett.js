import React, { Fragment } from 'react'
import { useSelector } from 'react-redux'
import * as yup from 'yup'
import { Formik } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import DollyModal from '~/components/ui/modal/DollyModal'
import { MiljoVelger } from '~/components/miljoVelger/MiljoVelger'
import Formatters from '~/utils/DataFormatter'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { filterMiljoe } from '~/components/miljoVelger/MiljoeInfo/TilgjengeligeMiljoer'

export default function GjenopprettBestilling(props) {
	const { bestilling, closeModal } = props
	const tilgjengeligeEnvironments = useSelector(state => state.environments.data)

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
		<DollyModal isOpen={true} closeModal={closeModal} width="25%" overflow="auto">
			<div style={{ paddingLeft: 20, paddingRight: 20 }}>
				<h1>Bestilling #{bestilling.id}</h1>
				<TitleValue title="Bestilt miljø" value={Formatters.arrayToString(environments)} />
				<br />
				<hr />
			</div>
			<Formik
				initialValues={{
					environments: filterMiljoe(tilgjengeligeEnvironments, environments)
				}}
				onSubmit={submitFormik}
				validationSchema={schemaValidation}
				render={formikProps => {
					return (
						<Fragment>
							<MiljoVelger
								bestillingsdata={bestilling.bestilling}
								heading="Velg miljø å gjenopprette i"
							/>

							<div className="dollymodal_buttons">
								<NavButton autoFocus onClick={closeModal}>
									Avbryt
								</NavButton>
								<NavButton type="hoved" onClick={formikProps.submitForm}>
									Utfør
								</NavButton>
							</div>
						</Fragment>
					)
				}}
			/>
		</DollyModal>
	)
}
