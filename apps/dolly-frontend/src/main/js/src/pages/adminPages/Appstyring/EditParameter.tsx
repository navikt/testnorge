import { PencilWritingIcon } from '@navikt/aksel-icons'
import { Button } from '@navikt/ds-react'
import React from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import DollyModal from '@/components/ui/modal/DollyModal'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormProvider, useForm } from 'react-hook-form'

export const EditParameter = ({ name, initialValue, getOptions }: any) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const formMethods = useForm({ defaultValues: { [name]: initialValue } })

	//TODO: Implementer lagring av verdi paa parameter
	const onSubmit = (data: any) => {
		console.log('Lagrer... ', data) //TODO - SLETT MEG
		closeModal()
	}

	const options = getOptions(name)

	return (
		<>
			<Button
				onClick={openModal}
				variant={'tertiary'}
				icon={<PencilWritingIcon />}
				size={'small'}
			/>
			<ErrorBoundary>
				<FormProvider {...formMethods}>
					<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'40%'} overflow={'auto'}>
						<div className="modal">
							<h1>Rediger parameter</h1>
							<br />
							<FormSelect name={name} label={name} options={options} size="grow" />
							<ModalActionKnapper
								submitknapp="Lagre"
								onSubmit={formMethods.handleSubmit(onSubmit)}
								onAvbryt={closeModal}
								center
							/>
						</div>
					</DollyModal>
				</FormProvider>
			</ErrorBoundary>
		</>
	)
}
