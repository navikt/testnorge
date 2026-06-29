import React, { useState } from 'react'
import * as Yup from 'yup'
import { DollyApi } from '@/service/Api'
import { useNavigate } from 'react-router'
import { usePdlOptions, useTestnorgeOptions } from '@/utils/hooks/useSelectOptions'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { FlyttPersonForm } from '@/components/ui/button/FlyttPersonButton/FlyttPersonForm'
import { Button, Dialog } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'

type FlyttPersonButtonTypes = {
	gruppeId: number
	open: boolean
	setOpen: Function
}

type Person = {
	ident: string
	master: string
}

type Option = {
	value: string
	label: string
	relasjoner: Array<string>
}

const validation = Yup.object().shape({
	identer: Yup.array().min(1, 'Velg minst én person').required(),
	gruppeId: Yup.string().required('Velg eksisterende gruppe eller opprett ny gruppe'),
})

const initialValues = { identer: [], gruppeId: undefined }

export const FlyttPersonModal = ({ gruppeId, open, setOpen }: FlyttPersonButtonTypes) => {
	const formMethods = useForm({
		defaultValues: initialValues,
		resolver: yupResolver(validation),
	})

	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null)

	const { identer: gruppeIdenter, loading: gruppeLoading } = useGruppeIdenter(gruppeId)

	const { data: pdlOptions, loading: pdlLoading, error: pdlError } = usePdlOptions(gruppeIdenter)

	const {
		data: testnorgeOptions,
		loading: testnorgeLoading,
		error: testnorgeError,
	} = useTestnorgeOptions(gruppeIdenter)

	const navigate = useNavigate()

	const getGruppeOptions = () => {
		const options = [] as Array<Option>
		gruppeIdenter?.forEach((person: Person) => {
			if (person.master === 'PDLF' && pdlOptions) {
				options.push(pdlOptions?.find((p: Option) => p.value === person.ident))
			} else if (person.master === 'PDL' && testnorgeOptions) {
				options.push(testnorgeOptions?.find((p: Option) => p.value === person.ident))
			}
		})
		return options
	}

	const gruppeOptions = getGruppeOptions()

	const gruppeIdenterListe = Array.isArray(gruppeOptions)
		? gruppeOptions?.map((person) => person?.value).filter((person) => person)
		: []

	const getRelatertePersoner = (identer: Array<string>, identerHentet = [] as Array<string>) => {
		const relatertePersonerHentet = identerHentet
		const identerNye = [] as Array<string>
		identer.forEach((ident: string) => {
			const funnetIdent = gruppeOptions?.find(
				(gruppeIdent: Option) =>
					gruppeIdent?.value === ident && !relatertePersonerHentet.includes(ident),
			)
			if (funnetIdent) {
				relatertePersonerHentet.push(funnetIdent.value)
				funnetIdent.relasjoner && identerNye.push(...funnetIdent.relasjoner)
			}
		})
		if (identerNye.length > 0) {
			getRelatertePersoner(identerNye, relatertePersonerHentet)
		}
		return relatertePersonerHentet
	}

	const harRelatertePersoner = (identer: Array<string>) => {
		if (!identer || identer?.length < 1) {
			return false
		}
		let relatert = false
		identer.forEach((ident) => {
			if (
				gruppeOptions?.find((option: Option) => option?.value === ident)?.relasjoner?.length > 0
			) {
				relatert = true
			}
		})
		return relatert
	}

	const handleSubmit = () => {
		setLoading(true)
		const { identer, gruppeId } = formMethods.getValues()
		const relasjoner = getRelatertePersoner(identer)
		const identerSamlet = Array.from(new Set([...identer, ...relasjoner]))
		DollyApi.flyttPersonerTilGruppe(gruppeId, identerSamlet)
			.then(() => {
				navigate(`../gruppe/${gruppeId}`)
			})
			.then(() => setOpen(false))
			.catch((e: any) => {
				setError(e.message)
			})
			.finally(() => setLoading(false))
	}

	const handleOpenChange = (isOpen: boolean) => {
		if (!loading) {
			setOpen(isOpen)
			formMethods.reset()
			setError(null)
		}
	}

	return (
		<Dialog open={open} onOpenChange={handleOpenChange}>
			<Dialog.Popup width="large" closeOnOutsideClick={false}>
				<Dialog.Header>
					<Dialog.Title>Flytt personer til gruppe</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<FormProvider {...formMethods}>
						<Form id="flytt-person-form" onSubmit={handleSubmit}>
							<FlyttPersonForm
								gruppeId={gruppeId}
								gruppeLoading={gruppeLoading}
								error={error}
								gruppeIdenterListe={gruppeIdenterListe}
								gruppeOptions={gruppeOptions}
								pdlLoading={pdlLoading}
								pdlError={pdlError}
								testnorgeLoading={testnorgeLoading}
								testnorgeError={testnorgeError}
								harRelatertePersoner={harRelatertePersoner}
							/>
						</Form>
					</FormProvider>
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button
							type="button"
							variant="secondary"
							data-testid={TestComponentSelectors.BUTTON_FLYTT_PERSONER_AVBRYT}
						>
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Button form="flytt-person-form" loading={loading}>
						Flytt personer
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
