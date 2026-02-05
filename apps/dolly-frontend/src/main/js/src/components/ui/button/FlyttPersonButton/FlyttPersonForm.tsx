import React, { useState } from 'react'
import Button from '@/components/ui/button/Button'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import styled from 'styled-components'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgGruppe'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollyErrorMessageWrapper } from '@/utils/DollyErrorMessageWrapper'
import Loading from '@/components/ui/loading/Loading'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import Icon from '@/components/ui/icon/Icon'
import { Alert } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useFieldArray, useFormContext } from 'react-hook-form'

type Option = {
	value: string
	label: string
	relasjoner: Array<string>
}

const PersonvelgerCheckboxes = styled.div`
	overflow-y: auto;
	max-height: 15rem;
	border: 1px solid #ccc;
	border-radius: 4px;
	padding: 10px;
`

const ValgtePersonerList = styled.div`
	overflow-y: auto;
	max-height: 15.4rem;
	display: block;
	margin-left: 20px;

	&& {
		ul {
			margin-top: 10px;
			line-height: 1.5;
		}
	}
`

const GruppeVelger = styled.div`
	padding-bottom: 10px;

	.error-message {
		margin-top: 10px;
	}

	.navds-button {
		margin-top: 10px;
	}
`

const PersonVelger = styled.div`
	display: flex;
	flex-wrap: wrap;
	border-top: 1px solid #ccc;
	padding-top: 10px;
`

const PersonKolonne = styled.div`
	display: flex;
	flex-direction: column;
	width: 50%;

	.navds-alert {
		margin-left: 20px;
		color: #368da8;
	}

	&&& {
		div {
			margin-right: 0;
		}
	}
`

const PersonSoek = styled.div`
	position: relative;

	&& {
		svg {
			position: absolute;
			top: 9px;
			right: 9px;
		}
	}
`

const StyledErrorMessageWithFocus = styled(DollyErrorMessageWrapper)`
	margin-top: 10px;
`
export const FlyttPersonForm = ({
	gruppeId,
	gruppeLoading,
	error,
	loading,
	gruppeIdenterListe,
	gruppeOptions,
	pdlLoading,
	pdlError,
	testnorgeLoading,
	testnorgeError,
	handleClose,
	harRelatertePersoner,
}: any) => {
	const formMethods = useFormContext()
	const [searchText, setSearchText] = useState('')
	const fieldMethods = useFieldArray({
		control: formMethods.control,
		name: 'identer',
	})
	const isChecked = (id: string) => fieldMethods.fields?.find((i: any) => i.fnr === id)
	const onClick = (e: { target: any }) => {
		const id = e.target.id
		isChecked(id)
			? fieldMethods.remove(fieldMethods.fields?.map((value: any) => value.fnr).indexOf(id))
			: fieldMethods.append({ fnr: id })
		formMethods.trigger('identer')
	}

	return (
		<>
			<h1>Flytt personer til gruppe</h1>
			<GruppeVelger>
				<VelgGruppe
					formMethods={formMethods}
					title={'Velg hvilken gruppe du ønsker å flytte personer til'}
					fraGruppe={gruppeId}
				/>
			</GruppeVelger>
			<PersonVelger>
				<PersonKolonne>
					<div className="flexbox--align-center">
						<h2>Velg personer</h2>
						<Hjelpetekst>
							Personer vil bli flyttet til valgt gruppe. Dersom valgte personer har relaterte
							personer vil disse også bli flyttet.
						</Hjelpetekst>
					</div>
					<PersonSoek>
						<DollyTextInput
							name="search"
							value={searchText}
							onChange={(e: any) => setSearchText(e.target.value)}
							size="grow"
							placeholder="Søk etter person"
						/>
						<Icon kind="search" size={20} />
					</PersonSoek>
					{!gruppeOptions ||
					gruppeOptions?.length < 1 ||
					gruppeOptions.every((i: any) => i === undefined) ? (
						pdlError && testnorgeError ? (
							<div className="error-message" style={{ marginBottom: '5px' }}>
								Henting av personer feilet helt eller delvis
							</div>
						) : (
							<Loading label="Laster personer..." />
						)
					) : (
						<PersonvelgerCheckboxes>
							{gruppeOptions?.map((person: Option) => {
								if (person?.label?.toUpperCase().includes(searchText?.toUpperCase())) {
									return (
										<div key={person.value}>
											<DollyCheckbox
												key={person.value}
												id={person.value}
												label={person.label}
												checked={fieldMethods.fields
													.map((val: any) => val.fnr)
													?.includes(person.value)}
												onChange={onClick}
												size="small"
												attributtCheckbox
											/>
										</div>
									)
								}
							})}
							{(gruppeLoading || pdlLoading || testnorgeLoading) && (
								<Loading label="Laster personer..." />
							)}
						</PersonvelgerCheckboxes>
					)}
					<div className="flexbox--flex-wrap" style={{ marginTop: '10px', gap: '10px' }}>
						<Button
							onClick={() => {
								formMethods.setValue('identer', gruppeIdenterListe)
								formMethods.trigger('identer')
							}}
						>
							VELG ALLE
						</Button>
						<Button
							data-testid={TestComponentSelectors.BUTTON_FLYTT_PERSONER_NULLSTILL}
							onClick={() => {
								formMethods.setValue('identer', [])
								formMethods.trigger('identer')
							}}
						>
							NULLSTILL
						</Button>
					</div>
					<StyledErrorMessageWithFocus name="identer" />
				</PersonKolonne>
				<PersonKolonne>
					<h2 style={{ marginLeft: '20px' }}>Valgte personer</h2>
					{harRelatertePersoner(formMethods.getValues('identer')) && (
						<Alert variant="info" size="small" inline>
							Du har valgt én eller flere personer som har relaterte personer. Disse vil også
							flyttes.
						</Alert>
					)}
					<ValgtePersonerList data-testid={TestComponentSelectors.CONTAINER_VALGTE_PERSONER}>
						{fieldMethods.fields.length > 0 ? (
							<ul>
								{fieldMethods.fields?.map((field: any) => (
									<li key={field.id}>
										{gruppeOptions?.find((person: Option) => person?.value === field.fnr)?.label}
									</li>
								))}
							</ul>
						) : (
							<span className="utvalg--empty-result">Ingenting er valgt</span>
						)}
					</ValgtePersonerList>
				</PersonKolonne>
			</PersonVelger>

			<div className="flexbox--full-width">
				{error && <div className="error-message">{`Feil: ${error}`}</div>}
			</div>
			<div className="flexbox--justify-center" style={{ marginTop: '15px' }}>
				<NavButton
					data-testid={TestComponentSelectors.BUTTON_FLYTT_PERSONER_AVBRYT}
					onClick={handleClose}
					variant="secondary"
				>
					Avbryt
				</NavButton>
				<NavButton
					onClick={() => formMethods.handleSubmit()}
					variant="primary"
					disabled={loading}
					loading={loading}
					style={{ marginLeft: '10px' }}
					type="submit"
				>
					Flytt personer
				</NavButton>
			</div>
		</>
	)
}
