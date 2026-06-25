import React, { useState } from 'react'
import styled from 'styled-components'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgGruppe'
import { DollyErrorMessageWrapper } from '@/utils/DollyErrorMessageWrapper'
import Loading from '@/components/ui/loading/Loading'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { Alert, Box, Button, Checkbox, CheckboxGroup, Search } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useFormContext } from 'react-hook-form'

type Option = {
	value: string
	label: string
	relasjoner: Array<string>
}

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
	max-height: 25rem;

	.aksel-alert {
		margin-left: 20px;
		margin-bottom: 10px;
		color: var(--ax-text-info-subtle);
	}

	&&& {
		div {
			margin-right: 0;
		}
	}
`

const FormLabel = styled.h3`
	font-size: var(--ax-font-size-large);
	font-weight: var(--ax-font-weight-bold);
`

const StyledErrorMessageWithFocus = styled(DollyErrorMessageWrapper)`
	p {
		margin: 5px 0 0 0;
	}
`
export const FlyttPersonForm = ({
	gruppeId,
	gruppeLoading,
	error,
	gruppeIdenterListe,
	gruppeOptions,
	pdlLoading,
	pdlError,
	testnorgeLoading,
	testnorgeError,
	harRelatertePersoner,
}: any) => {
	const formMethods = useFormContext()
	const [searchText, setSearchText] = useState('')

	const handleChangeIdenter = (valgteIdenter: Array<string>) => {
		formMethods.setValue('identer', valgteIdenter)
		formMethods.trigger()
	}

	const identer = formMethods.watch('identer')

	return (
		<>
			<VelgGruppe formMethods={formMethods} fraGruppe={gruppeId} />
			<PersonVelger>
				<PersonKolonne>
					<div className="flexbox--align-center">
						<FormLabel>Velg personer</FormLabel>
						<Hjelpetekst>
							Personer vil bli flyttet til valgt gruppe. Dersom valgte personer har relaterte
							personer vil disse også bli flyttet.
						</Hjelpetekst>
					</div>
					<Search
						label="Søk etter person"
						placeholder="Søk etter person ..."
						variant="simple"
						size="small"
						onChange={(value: any) => setSearchText(value)}
					/>
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
						<Box
							padding="space-12"
							borderWidth="1"
							borderColor="neutral"
							borderRadius="8"
							overflowY="auto"
							maxHeight="15rem"
							style={{ margin: '20px 0 10px 0' }}
						>
							<CheckboxGroup
								legend="Velg personer"
								hideLegend
								onChange={handleChangeIdenter}
								size="small"
								value={identer}
							>
								{gruppeOptions?.map((person: Option) => {
									if (person?.label?.toUpperCase().includes(searchText?.toUpperCase())) {
										return (
											<Checkbox key={person.value} value={person.value}>
												{person.label}
											</Checkbox>
										)
									}
								})}
							</CheckboxGroup>
							{(gruppeLoading || pdlLoading || testnorgeLoading) && (
								<Loading label="Laster personer..." />
							)}
						</Box>
					)}
					<div className="flexbox--flex-wrap">
						<Button
							variant="tertiary"
							size="small"
							onClick={() => {
								formMethods.setValue('identer', gruppeIdenterListe)
								formMethods.trigger('identer')
							}}
						>
							Velg alle
						</Button>
						<Button
							data-testid={TestComponentSelectors.BUTTON_FLYTT_PERSONER_NULLSTILL}
							variant="tertiary"
							size="small"
							onClick={() => {
								formMethods.setValue('identer', [])
								formMethods.trigger('identer')
							}}
						>
							Nullstill
						</Button>
					</div>
					<StyledErrorMessageWithFocus name="identer" />
				</PersonKolonne>
				<PersonKolonne>
					<FormLabel style={{ marginLeft: '20px' }}>Valgte personer</FormLabel>
					{harRelatertePersoner(formMethods.getValues('identer')) && (
						<Alert variant="info" size="small" inline>
							Du har valgt én eller flere personer som har relaterte personer. Disse vil også
							flyttes.
						</Alert>
					)}
					<ValgtePersonerList data-testid={TestComponentSelectors.CONTAINER_VALGTE_PERSONER}>
						{identer.length > 0 ? (
							<ul>
								{identer.map((ident: any) => (
									<li key={ident}>
										{gruppeOptions?.find((person: Option) => person?.value === ident)?.label}
									</li>
								))}
							</ul>
						) : (
							<span className="utvalg--empty-result">Ingen personer er valgt</span>
						)}
					</ValgtePersonerList>
				</PersonKolonne>
			</PersonVelger>
			<div className="flexbox--full-width">
				{error && <div className="error-message">{`Feil: ${error}`}</div>}
			</div>
		</>
	)
}
