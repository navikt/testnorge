import { useSelector } from 'react-redux'
import DollyModal from '~/components/ui/modal/DollyModal'
import { Formik } from 'formik'
import { filterMiljoe } from '~/components/miljoVelger/MiljoeInfo/TilgjengeligeMiljoer'
import React, { Fragment } from 'react'
import { MiljoVelger } from '~/components/miljoVelger/MiljoVelger'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type GjenopprettModal = {
	gjenopprettHeader: any
	environments?: Array<string>
	submitFormik: any
	closeModal: any
	bestilling?: any
}

export const GjenopprettModal = ({
	gjenopprettHeader,
	environments,
	submitFormik,
	closeModal,
	bestilling
}: GjenopprettModal) => {
	const tilgjengeligeEnvironments = useSelector((state: any) => state.environments.data)

	const schemaValidation = yup.object().shape({
		environments: yup.array().required('Velg minst ett miljø')
	})

	return (
		<DollyModal isOpen={true} closeModal={closeModal} width="50%" overflow="auto">
			<ErrorBoundary>
				{gjenopprettHeader}
				<Formik
					initialValues={{
						environments: filterMiljoe(tilgjengeligeEnvironments, environments)
					}}
					onSubmit={submitFormik}
					validationSchema={schemaValidation}
				>
					{formikProps => {
						return (
							<Fragment>
								<MiljoVelger
									bestillingsdata={bestilling ? bestilling.bestilling : null}
									heading="Velg miljø å gjenopprette i"
								/>

								<div className="dollymodal_buttons">
									<NavButton type={'fare'} onClick={closeModal}>
										Avbryt
									</NavButton>
									<NavButton type="hoved" onClick={formikProps.submitForm}>
										Utfør
									</NavButton>
								</div>
							</Fragment>
						)
					}}
				</Formik>
			</ErrorBoundary>
		</DollyModal>
	)
}
