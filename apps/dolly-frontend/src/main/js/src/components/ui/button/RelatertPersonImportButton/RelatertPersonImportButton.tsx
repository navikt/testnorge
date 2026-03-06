import React, { useState } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Button from '@/components/ui/button/Button'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import { DollyApi } from '@/service/Api'
import './RelatertPersonImportButton.less'
import { allCapsToCapitalized } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { FormProvider, useForm } from 'react-hook-form'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { Checkbox, CheckboxGroup } from '@navikt/ds-react'
import { UseFormReturn } from 'react-hook-form/dist/types'

type RelatertPersonProps = {
	type: string
	id: string
}

type IdentFormProps = {
	formMethods: UseFormReturn
	relatertPersonIdenter: Array<RelatertPersonProps>
	gruppeIdenter: string[]
	closeModal: () => void
	handleImport: (identer: string[]) => Promise<void>
}

type Props = {
	relatertPersonIdenter: Array<RelatertPersonProps>
	gruppeId: string
	gruppeIdenter: string[]
	master: string
}

const IdentForm = ({
	formMethods,
	relatertPersonIdenter,
	gruppeIdenter,
	closeModal,
	handleImport,
}: IdentFormProps) => {
	const handleChange = (identer: string[]) => {
		formMethods.setValue('identer', identer)
	}

	return (
		<div className="checkbox-form">
			<CheckboxGroup
				legend="Velg hvilke relaterte personer du ønsker å importere"
				style={{ marginTop: '25px' }}
				onChange={handleChange}
			>
				{relatertPersonIdenter.map((ident) => {
					const label = `${allCapsToCapitalized(ident.type)} (${ident.id})`
					const disabledCheckbox = gruppeIdenter?.includes(ident.id)
					return (
						<Checkbox
							key={ident.id}
							value={ident.id}
							disabled={disabledCheckbox}
							description={disabledCheckbox ? 'Person finnes allerede i gruppen' : undefined}
						>
							{label}
						</Checkbox>
					)
				})}
			</CheckboxGroup>
			<ModalActionKnapper
				submitknapp="Importer"
				onSubmit={() => {
					handleImport(formMethods.getValues()?.identer)
					closeModal()
				}}
				onAvbryt={closeModal}
				center
			/>
		</div>
	)
}

export const RelatertPersonImportButton = ({
	gruppeId,
	relatertPersonIdenter,
	gruppeIdenter,
	master,
}: Props) => {
	const [loading, setLoading] = useState(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [feilmelding, setFeilmelding] = useState<string | null>(null)
	const [fullfoert, setFullfoert] = useState(false)
	const mutate = useMatchMutate()
	const formMethods = useForm({ mode: 'onBlur', defaultValues: { identer: [] as string[] } })

	if (!relatertPersonIdenter) {
		return null
	}

	const disabled =
		!gruppeIdenter || relatertPersonIdenter?.every((ident) => gruppeIdenter?.includes(ident.id))

	const foersteRelatertPersonType = _.lowerCase(relatertPersonIdenter[0]?.type)

	const handleImport = async (identer = null as unknown as string[]) => {
		setLoading(true)
		setFeilmelding(null)
		const results = await Promise.allSettled(
			identer.map(
				(ident) => DollyApi.importerRelatertPerson(gruppeId, ident, master), // return the promise
			),
		)
		const hasFailure = results.some((r) => r.status === 'rejected')
		if (hasFailure) {
			setFeilmelding('Noe gikk galt')
			setFullfoert(false)
		} else {
			setFullfoert(true)
			mutate(REGEX_BACKEND_GRUPPER)
		}
		setLoading(false)
	}

	const handleCloseModal = () => {
		closeModal()
		setFeilmelding(null)
		formMethods.reset()
	}

	if (loading) {
		return <Loading label="importerer..." />
	}

	if (fullfoert) {
		return (
			<div className={'success-text'}>
				<Icon size={16} kind={'feedback-check-circle'} />
				<span>VENNLIGST LUKK VISNING</span>
			</div>
		)
	}

	return (
		<FormProvider {...formMethods}>
			<div>
				<Button
					onClick={openModal}
					disabled={disabled}
					title={disabled ? 'Relaterte personer er allerede i gruppen' : ''}
					kind="relasjoner"
					className="svg-icon-blue"
				>
					{relatertPersonIdenter.length > 1
						? 'IMPORTER RELATERTE PERSONER'
						: `IMPORTER ${relatertPersonIdenter[0]?.type}`}
				</Button>
				{feilmelding && (
					<div className="error-message" style={{ margin: '5px 0 0 30px' }}>
						{feilmelding}
					</div>
				)}
				<DollyModal isOpen={modalIsOpen} closeModal={handleCloseModal} width="40%" overflow="auto">
					<div className="modal">
						{relatertPersonIdenter.length > 1 ? (
							<>
								<h1>Importer relaterte personer</h1>
								<IdentForm
									formMethods={formMethods}
									gruppeIdenter={gruppeIdenter}
									closeModal={handleCloseModal}
									relatertPersonIdenter={relatertPersonIdenter}
									handleImport={handleImport}
								/>
							</>
						) : (
							<>
								<h1>{`Importer ${foersteRelatertPersonType}`}</h1>
								<h4>
									{`Er du sikker på at du vil importere og legge til valgt persons ${
										foersteRelatertPersonType || 'relaterte person'
									} i gruppen?`}
								</h4>
								<ModalActionKnapper
									submitknapp="Ja"
									onSubmit={() => {
										handleImport([relatertPersonIdenter[0]?.id])
										handleCloseModal()
									}}
									onAvbryt={handleCloseModal}
									center
								/>
							</>
						)}
					</div>
				</DollyModal>
			</div>
		</FormProvider>
	)
}
