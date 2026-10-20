import React, { useState } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyApi } from '@/service/Api'
import { allCapsToCapitalized } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { FormProvider, useForm } from 'react-hook-form'
import { BodyLong, Button, Checkbox, CheckboxGroup, Dialog, InlineMessage } from '@navikt/ds-react'
import { PersonTallShortIcon } from '@navikt/aksel-icons'

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
	const [feilmelding, setFeilmelding] = useState<string | null>(null)

	const mutate = useMatchMutate()
	const formMethods = useForm({ mode: 'onBlur', defaultValues: { identer: [] as string[] } })

	if (!relatertPersonIdenter) {
		return null
	}

	const disabled =
		!gruppeIdenter || relatertPersonIdenter?.every((ident) => gruppeIdenter?.includes(ident.id))

	const foersteRelatertPersonType = _.lowerCase(relatertPersonIdenter[0]?.type)

	const handleChange = (identer: string[]) => {
		formMethods.setValue('identer', identer)
	}

	const importerTitle =
		relatertPersonIdenter.length > 1
			? 'Importer relaterte personer'
			: `Importer ${relatertPersonIdenter[0]?.type?.toLowerCase()}`

	return (
		<>
			<Button
				size="xsmall"
				variant="tertiary"
				icon={<PersonTallShortIcon aria-hidden />}
				onClick={openModal}
				disabled={disabled}
				title={disabled ? 'Relaterte personer er allerede i gruppen' : ''}
			>
				{importerTitle}
			</Button>
			<Dialog open={modalIsOpen} onOpenChange={modalIsOpen ? closeModal : openModal}>
				<Dialog.Popup>
					<Dialog.Header>
						<Dialog.Title>{importerTitle}</Dialog.Title>
					</Dialog.Header>
					<Dialog.Body>
						<FormProvider {...formMethods}>
							<form
								id="importer-relatert-person"
								onSubmit={formMethods.handleSubmit(async (data) => {
									setLoading(true)
									const identer =
										relatertPersonIdenter.length > 1
											? data?.identer
											: [relatertPersonIdenter[0]?.id]
									setFeilmelding(null)
									const results = await Promise.allSettled(
										identer.map((ident) =>
											DollyApi.importerRelatertPerson(gruppeId, ident, master),
										),
									)
									const hasFailure = results.some((r) => r.status === 'rejected')
									if (hasFailure) {
										setFeilmelding('Noe gikk galt')
									} else {
										mutate(REGEX_BACKEND_GRUPPER)
										closeModal()
									}
									setLoading(false)
								})}
							>
								{relatertPersonIdenter.length > 1 ? (
									<CheckboxGroup
										legend="Velg hvilke relaterte personer du ønsker å importere"
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
													description={
														disabledCheckbox ? 'Person finnes allerede i gruppen' : undefined
													}
												>
													{label}
												</Checkbox>
											)
										})}
									</CheckboxGroup>
								) : (
									<BodyLong>
										{`Er du sikker på at du vil importere og legge til valgt persons ${
											foersteRelatertPersonType || 'relaterte person'
										} i gruppen?`}
									</BodyLong>
								)}
								{feilmelding && <InlineMessage status="error">{feilmelding}</InlineMessage>}
							</form>
						</FormProvider>
					</Dialog.Body>
					<Dialog.Footer>
						<Dialog.CloseTrigger>
							<Button type="button" variant="secondary">
								Avbryt
							</Button>
						</Dialog.CloseTrigger>
						<Button form="importer-relatert-person" loading={loading}>
							Importer
						</Button>
					</Dialog.Footer>
				</Dialog.Popup>
			</Dialog>
		</>
	)
}
