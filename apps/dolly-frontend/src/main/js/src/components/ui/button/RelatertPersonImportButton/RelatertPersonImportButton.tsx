import React, { useState } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Button from '@/components/ui/button/Button'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import { DollyApi } from '@/service/Api'
import './RelatertPersonImportButton.less'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { allCapsToCapitalized } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { FormProvider, useFieldArray, useForm } from 'react-hook-form'

type RelatertPersonProps = {
	type: string
	id: string
}

type Props = {
	relatertPersonIdenter: Array<RelatertPersonProps>
	gruppeId: string
	gruppeIdenter: string[]
	master: string
}

export const RelatertPersonImportButton = ({
	gruppeId,
	relatertPersonIdenter,
	gruppeIdenter,
	master,
}: Props) => {
	const [loading, setLoading] = useState(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [feilmelding, setFeilmelding] = useState(null)
	const [fullfoert, setFullfoert] = useState(false)
	const mutate = useMatchMutate()
	const formMethods = useForm({ mode: 'onBlur', defaultValues: { identer: [] } })
	const fieldMethods = useFieldArray({ control: formMethods.control, name: 'identer' })

	if (!relatertPersonIdenter) {
		return null
	}

	const disabled =
		!gruppeIdenter || relatertPersonIdenter?.every((ident) => gruppeIdenter?.includes(ident.id))

	const foersteRelatertPersonType = _.lowerCase(relatertPersonIdenter[0]?.type)

	const handleImport = async (identer = null as unknown as string[]) => {
		setLoading(true)
		setFeilmelding(null)

		await Promise.allSettled(
			identer.map((ident) => {
				DollyApi.importerRelatertPerson(gruppeId, ident, master)
					.then((_response) => {
						setLoading(false)
						setFullfoert(true)
						mutate(REGEX_BACKEND_GRUPPER)
					})
					.catch((_error) => {
						setFeilmelding('Noe gikk galt')
						setFullfoert(false)
						setLoading(false)
					})
			}),
		)
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

	const identCheckbox = (values: Array<string>, onClick: Function) => {
		return (
			<div className="miljo-velger_checkboxes">
				{relatertPersonIdenter.map((ident) => {
					const disabledCheckbox = gruppeIdenter?.includes(ident.id)
					return (
						<div key={ident.id} title={disabledCheckbox ? 'Person er allerede i gruppen' : ''}>
							<DollyCheckbox
								key={ident.id}
								id={ident.id}
								label={`${allCapsToCapitalized(ident.type)} (${ident.id})`}
								checked={values?.includes(ident.id)}
								onChange={onClick}
								size={'grow'}
								disabled={disabledCheckbox}
								attributtCheckbox
							/>
						</div>
					)
				})}
			</div>
		)
	}

	const identForm = (fieldMethods: any) => {
		const values = fieldMethods.fields?.values?.identer
		const isChecked = (id: string) => values?.includes(id)
		const onClick = (e: { target: RelatertPersonProps }) => {
			const { id } = e.target
			isChecked(id) ? fieldMethods.remove(values?.indexOf(id)) : fieldMethods.append(id)
		}
		return (
			<>
				<div className="relatertPersonImportModal-content">{identCheckbox(values, onClick)}</div>
				<div className="relatertPersonImportModal-actions">
					<NavButton onClick={closeModal} variant={'secondary'}>
						Avbryt
					</NavButton>
					<NavButton
						onClick={() => {
							closeModal()
							handleImport(formMethods.getValues()?.identer)
						}}
						variant={'primary'}
					>
						Importer
					</NavButton>
				</div>
			</>
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
				<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
					<div className="relatertPersonImportModal">
						<Icon size={50} kind="personinformasjon" />
						{relatertPersonIdenter.length > 1 ? (
							<>
								<h1>Importer relaterte personer</h1>
								<h4>Velg hvilke relaterte personer du ønsker å importere</h4>
								{identForm(fieldMethods)}
							</>
						) : (
							<>
								<div className="relatertPersonImportModal-content-center">
									<h1>{`Importer ${foersteRelatertPersonType}`}</h1>
									<h4>
										{`Er du sikker på at du vil importere og legge til valgt persons ${
											foersteRelatertPersonType || 'relaterte person'
										} i gruppen?`}
									</h4>
								</div>
								<div className="relatertPersonImportModal-actions">
									<NavButton onClick={closeModal} variant={'secondary'}>
										Nei
									</NavButton>
									<NavButton
										onClick={() => {
											closeModal()
											handleImport([relatertPersonIdenter[0]?.id])
										}}
										variant={'primary'}
									>
										Ja
									</NavButton>
								</div>
							</>
						)}
					</div>
				</DollyModal>
			</div>
		</FormProvider>
	)
}
