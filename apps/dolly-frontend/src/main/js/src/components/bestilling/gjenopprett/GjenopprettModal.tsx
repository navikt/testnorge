import DollyModal from '@/components/ui/modal/DollyModal'
import { filterMiljoe } from '@/components/miljoVelger/MiljoeInfo'
import React, { Fragment } from 'react'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'

type GjenopprettModalProps = {
	gjenopprettHeader: any
	environments?: Array<string>
	submitFormik: any
	closeModal: any
	bestilling?: any
	brukertype?: string
}

export const GjenopprettModal = ({
	gjenopprettHeader,
	environments,
	submitFormik,
	closeModal,
	bestilling,
	brukertype,
}: GjenopprettModalProps) => {
	const { dollyEnvironments: tilgjengeligeEnvironments } = useDollyEnvironments()
	const schemaValidation = yup.object().shape({
		environments: yup.array().required('Velg minst ett miljø'),
	})
	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: { environments: filterMiljoe(tilgjengeligeEnvironments, environments) },
		resolver: yupResolver(schemaValidation),
	})

	return (
		<DollyModal isOpen={true} closeModal={closeModal} width="50%" overflow="auto">
			<ErrorBoundary>
				{gjenopprettHeader}
				<Fragment>
					<MiljoVelger
						bestillingsdata={bestilling ? bestilling.bestilling : null}
						heading="Velg miljø å gjenopprette i"
						bankIdBruker={brukertype && brukertype === 'BANKID'}
						alleredeValgtMiljoe={[]}
					/>

					<div className="dollymodal_buttons">
						<NavButton variant={'danger'} onClick={closeModal}>
							Avbryt
						</NavButton>
						<NavButton variant={'primary'} onClick={() => formMethods.handleSubmit(submitFormik)}>
							Utfør
						</NavButton>
					</div>
				</Fragment>
			</ErrorBoundary>
		</DollyModal>
	)
}
