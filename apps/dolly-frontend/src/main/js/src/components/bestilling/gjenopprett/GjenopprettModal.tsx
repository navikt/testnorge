import { DollyModal } from '@/components/ui/modal/DollyModal'
import React, { Fragment } from 'react'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Alert } from '@navikt/ds-react'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { filterMiljoe, gyldigeDollyMiljoer } from '@/components/miljoVelger/MiljoVelgerUtils'
import { useOrganisasjonMiljoe } from '@/utils/hooks/useOrganisasjonTilgang'

type GjenopprettModalProps = {
	gjenopprettHeader: any
	environments?: Array<string>
	submitForm: any
	closeModal: any
	bestilling?: any
}

export const GjenopprettModal = ({
	gjenopprettHeader,
	environments,
	submitForm,
	closeModal,
	bestilling,
}: GjenopprettModalProps) => {
	const { currentBruker } = useCurrentBruker()

	const { organisasjonMiljoe } = useOrganisasjonMiljoe()
	const tilgjengeligeMiljoer = organisasjonMiljoe?.miljoe

	const { dollyEnvironments, loading } = useDollyEnvironments()
	const gyldigeEnvironments = gyldigeDollyMiljoer(dollyEnvironments)

	const defaultEnvironments = filterMiljoe(gyldigeEnvironments, environments, tilgjengeligeMiljoer)

	const schemaValidation = yup.object().shape({
		environments: yup.array().required('Velg minst ett miljø'),
	})

	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: { environments: defaultEnvironments },
		resolver: yupResolver(schemaValidation),
	})

	const getWarningText = () => {
		if (
			bestilling?.bestilling?.arbeidsplassenCV &&
			!bestilling?.bestilling?.arbeidssoekerregisteret
		) {
			return 'Gjenoppretting av Nav CV vil ikke fungere om person mangler oppføring i Arbeidssøkerregisteret.'
		}
		return null
	}

	const warningText = getWarningText()

	return (
		<FormProvider {...formMethods}>
			<DollyModal isOpen={true} closeModal={closeModal} width="50%" overflow="auto">
				<ErrorBoundary>
					{gjenopprettHeader}
					<Fragment>
						<MiljoVelger
							bestillingsdata={bestilling ? bestilling.bestilling : null}
							heading="Velg miljø å gjenopprette i"
							currentBruker={currentBruker}
							gyldigeMiljoer={gyldigeEnvironments}
							tilgjengeligeMiljoer={tilgjengeligeMiljoer}
							loading={loading}
						/>
						{warningText && (
							<div style={{ padding: '0 20px' }}>
								<Alert variant={'warning'}>{warningText}</Alert>
							</div>
						)}
						<div className="dollymodal_buttons">
							<NavButton variant={'danger'} onClick={closeModal}>
								Avbryt
							</NavButton>
							<NavButton
								data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER}
								variant={'primary'}
								onClick={formMethods.handleSubmit(submitForm)}
							>
								Utfør
							</NavButton>
						</div>
					</Fragment>
				</ErrorBoundary>
			</DollyModal>
		</FormProvider>
	)
}
