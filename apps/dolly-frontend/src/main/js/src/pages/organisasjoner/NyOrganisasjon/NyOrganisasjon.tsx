import React, { useState } from 'react'
import { useToggle } from 'react-use'
import { NavLink } from 'react-router-dom'
import Button from '@/components/ui/button/Button'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'

import styled from 'styled-components'
import { useDollyOrganisasjonMaler } from '@/utils/hooks/useMaler'
import {
	getBrukerOptions,
	getMalOptions,
	NyBestillingProps,
} from '@/components/bestillingsveileder/startModal/NyIdent/NyIdent'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { Form, FormProvider, useForm } from 'react-hook-form'

const initialValues = {
	mal: null as unknown as string,
}

const InputDiv = styled.div`
	margin-top: 10px;
`

export const NyOrganisasjon = ({ onAvbryt, onSubmit, brukernavn }: NyBestillingProps) => {
	const [bruker, setBruker] = useState(brukernavn)
	const [malAktiv, toggleMalAktiv] = useToggle(false)
	const { maler, loading } = useDollyOrganisasjonMaler()
	const formMethods = useForm({ defaultValues: initialValues })

	const brukerOptions = getBrukerOptions(maler)
	const malOptions = getMalOptions(maler, bruker)

	const handleMalChange = (formMethods: UseFormReturn) => {
		toggleMalAktiv()
		if (formMethods.getValues()?.mal) {
			formMethods.setValue('mal', null)
		}
	}

	const handleBrukerChange = (event: { value: any }, formMethods: UseFormReturn) => {
		setBruker(event.value)
		formMethods.setValue('mal', null)
	}

	const preSubmit = (values: { mal: any }, formMethods: any) => {
		if (values.mal) values.mal = malOptions.find((m) => m.value === values.mal)?.data
		return onSubmit(values, formMethods)
	}

	return (
		<FormProvider {...formMethods}>
			<Form initialValues={initialValues} onSubmit={() => formMethods.handleSubmit(preSubmit)}>
				<div className="ny-bestilling-form">
					<div className="ny-bestilling-form_maler">
						<div>
							<DollyCheckbox
								name="aktiver-maler"
								onChange={() => handleMalChange(formMethods)}
								label="Opprett fra mal"
								wrapperSize={'none'}
								size={'small'}
								isSwitch
							/>
						</div>

						<InputDiv>
							<DollySelect
								name="zIdent"
								label="Bruker"
								isLoading={loading}
								options={brukerOptions}
								size="medium"
								onChange={(e: { value: any }) => handleBrukerChange(e, formMethods)}
								value={bruker}
								isClearable={false}
								isDisabled={!malAktiv}
							/>
							<FormikSelect
								name="mal"
								label="Maler"
								isLoading={loading}
								options={malOptions}
								size="grow"
								fastfield={false}
								isDisabled={!malAktiv}
							/>
						</InputDiv>
						<div className="mal-admin">
							<Button kind="maler" fontSize={'1.2rem'}>
								<NavLink to="/minside">Administrer maler</NavLink>
							</Button>
						</div>
					</div>
					<ModalActionKnapper
						data-cy={CypressSelector.BUTTON_START_BESTILLING}
						submitknapp="Start bestilling"
						disabled={!formMethods.formState.isValid || formMethods.formState.isSubmitting}
						onSubmit={() => formMethods.handleSubmit(preSubmit)}
						onAvbryt={onAvbryt}
						center
					/>
				</div>
			</Form>
		</FormProvider>
	)
}
