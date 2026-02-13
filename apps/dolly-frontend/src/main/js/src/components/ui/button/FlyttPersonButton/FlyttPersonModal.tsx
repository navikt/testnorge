import React, { useState } from 'react'
import * as Yup from 'yup'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import { DollyApi } from '@/service/Api'
import styled from 'styled-components'
import { useNavigate } from 'react-router'
import { usePdlOptions, useTestnorgeOptions } from '@/utils/hooks/useSelectOptions'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { FlyttPersonForm } from '@/components/ui/button/FlyttPersonButton/FlyttPersonForm'

type FlyttPersonButtonTypes = {
	gruppeId: number
	modalIsOpen: boolean
	closeModal: Function
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

const ModalContent = styled.div`
	display: flex;
	flex-direction: column;

	&&& {
		h2 {
			font-size: 1.2em;
		}
	}
`

export const FlyttPersonModal = ({ gruppeId, modalIsOpen, closeModal }: FlyttPersonButtonTypes) => {
	const formMethods = useForm({
		defaultValues: { identer: [], gruppeId: undefined },
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
		? gruppeOptions
				?.map((person) => person?.value)
				.filter((person) => person)
				.map((person) => ({ fnr: person }))
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

	const harRelatertePersoner = (identer: Array<{ fnr: string }>) => {
		if (!identer || identer?.length < 1) {
			return false
		}
		let relatert = false
		identer.forEach((ident) => {
			if (
				gruppeOptions?.find((option: Option) => option?.value === ident?.fnr)?.relasjoner?.length >
				0
			) {
				relatert = true
			}
		})
		return relatert
	}

	const handleSubmit = () => {
		const { identer, gruppeId } = formMethods.getValues()
		const identerFormatert = identer.map((ident) => ident.fnr)
		const relasjoner = getRelatertePersoner(identerFormatert)
		const identerSamlet = Array.from(new Set([...identerFormatert, ...relasjoner]))
		DollyApi.flyttPersonerTilGruppe(gruppeId, identerSamlet)
			.then(() => {
				closeModal()
				setLoading(false)
				navigate(`../gruppe/${gruppeId}`)
			})
			.catch((e: Error) => {
				setError(e.message)
				setLoading(false)
			})
	}

	const handleClose = () => {
		closeModal()
		setError(null)
		setLoading(false)
	}

	return (
		<DollyModal isOpen={modalIsOpen} closeModal={handleClose} minWidth="50%" overflow="auto">
			<ModalContent>
				<FormProvider {...formMethods}>
					<Form onSubmit={handleSubmit}>
						<FlyttPersonForm
							gruppeId={gruppeId}
							gruppeLoading={gruppeLoading}
							error={error}
							loading={loading}
							gruppeIdenterListe={gruppeIdenterListe}
							gruppeOptions={gruppeOptions}
							pdlLoading={pdlLoading}
							pdlError={pdlError}
							testnorgeLoading={testnorgeLoading}
							testnorgeError={testnorgeError}
							handleClose={handleClose}
							harRelatertePersoner={harRelatertePersoner}
						/>
					</Form>
				</FormProvider>
			</ModalContent>
		</DollyModal>
	)
}
