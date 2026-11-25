import { PencilWritingIcon } from '@navikt/aksel-icons'
import { Button, Dialog, Select } from '@navikt/ds-react'
import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { useForm } from 'react-hook-form'
import { FetchData } from '@/pages/adminPages/Levendearbeidsforhold/util/Typer'
import { csrfFetch } from '@/api/csrf'

interface Props {
	name: string
	label: string
	initialValue: string
	options: Array<string>
	data: Array<FetchData>
	setData: (data: Array<FetchData>) => void
}

export const EditParameter = ({ name, label, initialValue, options, data, setData }: Props) => {
	const [modalIsOpen, setModalIsOpen] = useState(false)
	const formId = `rediger-parameter-${name}`

	const {
		register,
		handleSubmit,
		formState: { errors },
	} = useForm<{ value: string }>({ defaultValues: { value: initialValue } })

	async function oppdaterParameterverdi(value: string) {
		await csrfFetch(`/testnav-levende-arbeidsforhold-ansettelse/api/v1/parameter/${name}`, {
			method: 'PUT',
			body: value,
		}).then((res) => (res.status === 200 ? onSubmit(value) : console.error('Feil feil feil')))
	}

	const validerParameter = (value: string | undefined): string | undefined => {
		if (name === 'antallOrganisasjoner') {
			const antallPersoner = data.find((obj) => obj.navn === 'antallPersoner')?.verdi
			if (!value) return 'Må settes'
			else if (Number.parseInt(value!) > Number.parseInt(antallPersoner!))
				return `Kan ikke være flere organisasjoner enn antall personer`
		} else if (name === 'antallPersoner') {
			const antallOrganisasjoner = data.find((obj) => obj.navn === 'antallOrganisasjoner')?.verdi
			if (!value) return 'Må settes'
			else if (Number.parseInt(value!) < Number.parseInt(antallOrganisasjoner!))
				return `Kan ikke være færre personer enn antall organisasjoner`
		}
		return undefined
	}

	const onSubmit = (value: string) => {
		const kopi = [...data]
		const nyttObjektIndex = kopi.findIndex((obj) => obj.navn === name)
		kopi[nyttObjektIndex] = { ...kopi[nyttObjektIndex], verdi: value }
		setData(kopi)
		setModalIsOpen(false)
	}

	return (
		<ErrorBoundary>
			<Dialog open={modalIsOpen} onOpenChange={setModalIsOpen}>
				<Dialog.Trigger>
					<Button variant="tertiary" icon={<PencilWritingIcon title="Rediger" />} size="small" />
				</Dialog.Trigger>
				<Dialog.Popup>
					<Dialog.Header>
						<Dialog.Title>Rediger parameter</Dialog.Title>
					</Dialog.Header>
					<Dialog.Body>
						<form
							id={formId}
							onSubmit={handleSubmit(({ value }) => {
								oppdaterParameterverdi(value)
							})}
						>
							<Select
								{...register('value', {
									validate: validerParameter,
								})}
								label={label}
								error={errors.value?.message}
							>
								{options.map((option, index) => (
									<option key={index} value={option}>
										{option}
									</option>
								))}
							</Select>
						</form>
					</Dialog.Body>
					<Dialog.Footer>
						<Dialog.CloseTrigger>
							<Button variant="secondary">Avbryt</Button>
						</Dialog.CloseTrigger>
						<Button type="submit" form={formId}>
							Lagre
						</Button>
					</Dialog.Footer>
				</Dialog.Popup>
			</Dialog>
		</ErrorBoundary>
	)
}
