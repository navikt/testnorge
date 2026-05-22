import React, { useState } from 'react'
import { erMiljouavhengig, MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import * as yup from 'yup'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Alert, BodyLong, Button, Dialog } from '@navikt/ds-react'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { filterMiljoe, gyldigeDollyMiljoer } from '@/components/miljoVelger/MiljoVelgerUtils'
import { useOrganisasjonMiljoe } from '@/utils/hooks/useOrganisasjonTilgang'
import { ArrowsCirclepathIcon } from '@navikt/aksel-icons'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString } from '@/utils/DataFormatter'

type GjenopprettModalProps = {
	environments?: Array<string>
	submitForm: any
	title: string
	beskrivelse?: string
	bestilling?: any
	antallIdenter?: number
	bestilteMiljoer?: Array<string>
	disabled?: boolean
	disabledTitle?: string
}

export const GjenopprettModal = ({
	environments,
	submitForm,
	title,
	beskrivelse,
	bestilling,
	antallIdenter,
	bestilteMiljoer,
	disabled = false,
	disabledTitle = undefined,
}: GjenopprettModalProps) => {
	const [open, setOpen] = useState(false)

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

	const miljoeUavhengig = erMiljouavhengig(bestilling?.bestilling || null)

	const gjenopprettHeader = (
		<div className="flexbox" style={{ margin: '10px 0 25px 0' }}>
			<TitleValue title="Antall identer" value={antallIdenter} />
			<TitleValue
				title="Bestilte miljøer"
				value={arrayToString(bestilteMiljoer)?.toUpperCase()}
				size="medium"
			/>
		</div>
	)

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_GJENOPPRETT_GRUPPE}
				size="xsmall"
				variant="tertiary"
				icon={<ArrowsCirclepathIcon aria-hidden />}
				onClick={() => setOpen(true)}
				disabled={disabled}
				title={disabledTitle}
			>
				Gjenopprett
			</Button>
			<Dialog open={open} onOpenChange={setOpen}>
				<Dialog.Popup>
					<Dialog.Header>
						<Dialog.Title>{title}</Dialog.Title>
					</Dialog.Header>
					<Dialog.Body>
						{(antallIdenter || bestilteMiljoer?.length > 0) && gjenopprettHeader}
						{beskrivelse && !miljoeUavhengig && (
							<BodyLong style={{ marginBottom: '25px' }}>{beskrivelse}</BodyLong>
						)}
						<FormProvider {...formMethods}>
							<form
								id="gjenopprett-form"
								onSubmit={formMethods.handleSubmit(async (...args) => {
									await submitForm(...args)
									setOpen(false)
								})}
							>
								<MiljoVelger
									bestillingsdata={bestilling ? bestilling.bestilling : null}
									currentBruker={currentBruker}
									gyldigeMiljoer={gyldigeEnvironments}
									tilgjengeligeMiljoer={tilgjengeligeMiljoer}
									miljoeLabel={'Velg miljø å gjenopprette i'}
									loading={loading}
								/>
								{warningText && (
									<div style={{ padding: '0 20px' }}>
										<Alert variant={'warning'}>{warningText}</Alert>
									</div>
								)}
							</form>
						</FormProvider>
					</Dialog.Body>
					<Dialog.Footer>
						<Dialog.CloseTrigger>
							<Button type="button" variant="secondary">
								Avbryt
							</Button>
						</Dialog.CloseTrigger>
						<Button
							data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER}
							form="gjenopprett-form"
							loading={false}
						>
							Gjenopprett
						</Button>
					</Dialog.Footer>
				</Dialog.Popup>
			</Dialog>
		</>
	)
}
