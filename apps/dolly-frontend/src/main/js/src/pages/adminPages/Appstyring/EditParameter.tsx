import { PencilWritingIcon } from '@navikt/aksel-icons'
import {Button, TextField} from '@navikt/ds-react'
import React, {useState} from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import DollyModal from '@/components/ui/modal/DollyModal'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormProvider, useForm } from 'react-hook-form'
import { Select} from "@navikt/ds-react"

interface APIKallBody{
	navn: string
	verdi: string
}

export const EditParameter = ({ options }: { options: Array<string>}) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	//const formMethods = useForm({ defaultValues: { [name]: initialValue } })

	const { register, handleSubmit, formState: { errors} } = useForm<{antallOrganisasjoner: string}>()


	async function oppdaterParameterverdi(parameternavn: string, parameterverdi: string) {
		const req = await fetch(`/testnav-levende-arbeidsforhold-ansettelsev2/api/oppdatereVerdier/${parameternavn}`,
			{method: "PUT", body: JSON.stringify(parameterverdi)});
	}

	//TODO: Implementer lagring av verdi paa parameter

	const onSubmit = (data: any) => {
		//oppdaterParameterverdi(param[0], param[1]);
		closeModal()
	}

	return (
		<>
			<Button
				onClick={openModal}
				variant={'tertiary'}
				icon={<PencilWritingIcon />}
				size={'small'}
			/>
			<ErrorBoundary>
					<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'40%'} overflow={'auto'}>
					<form onSubmit={handleSubmit(data => {
						console.log(data)
						oppdaterParameterverdi("antallOrganisasjoner", data.antallOrganisasjoner)
					})}>
						<div className="modal">
							<h1>Rediger parameter</h1>
							<br />
							<Select {...register('antallOrganisasjoner', { required: {
								value: true,
									message: 'Du må sette antall organisasjoner'
								}})} label="Antall organisasjoner" error={errors.antallOrganisasjoner?.message}>
								<option value="">Velg antall</option>
								{options.map((option, index) => (
									<option key={index} value={option}>{option}</option>
								))}
							</Select>
							<Button>Lagre</Button>
						</div>
				</form>
					</DollyModal>
			</ErrorBoundary>
		</>
	)
}
