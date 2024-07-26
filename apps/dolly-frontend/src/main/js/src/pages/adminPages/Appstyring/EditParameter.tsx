import {PencilWritingIcon} from '@navikt/aksel-icons'
import {Button, Select} from '@navikt/ds-react'
import React from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import DollyModal from '@/components/ui/modal/DollyModal'
import {ErrorBoundary} from '@/components/ui/appError/ErrorBoundary'
import {useForm} from 'react-hook-form'
import {FetchData} from "@/pages/adminPages/Appstyring/util/Typer";

interface Props {
	name: string,
	label: string
	initialValue: string,
	options: Array<string>
	data: Array<FetchData>
	setData: (data: Array<FetchData>) => void
}

export const EditParameter = ({name, label, initialValue, options, data, setData }: Props) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	//const formMethods = useForm({ defaultValues: { [name]: initialValue } })

	const { register, handleSubmit, formState: { errors} } = useForm<{value: string}>(
		{ defaultValues: {value: initialValue}})


	async function oppdaterParameterverdi(value: string) {
		await fetch(`/testnav-levende-arbeidsforhold-ansettelsev2/api/oppdatereVerdier/${name}`,
			{method: "PUT", body: JSON.stringify(value)}).then(res => res.status === 200 ? onSubmit(value) : console.error('Feil feil feil'));
	}

	const onSubmit = (value: string) => {
		const kopi = [...data]
		const nyttObjektIndex = kopi.findIndex(obj => obj.navn === name)
		kopi[nyttObjektIndex] = {...kopi[nyttObjektIndex], verdi: value}
		setData(kopi)
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
					<form onSubmit={handleSubmit(({ value}) => {
						console.log(value)
						oppdaterParameterverdi(value)
					})}>
						<div className="modal">
							<h1>Rediger parameter</h1>
							<br />
							<Select {...register("value", { required: {
								value: true,
									message: 'Du må velge en verdi'
								}})} label={label} error={errors.value?.message}>
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
