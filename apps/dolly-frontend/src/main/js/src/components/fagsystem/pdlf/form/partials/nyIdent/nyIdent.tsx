import * as React from 'react'
import { getInitialNyIdent } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { PdlPersonForm } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { NyIdentRegelInfo } from '@/components/fagsystem/pdlf/form/partials/nyIdent/NyIdentInfo'
import {
	getKonverterValg,
	getStandardIdenttype,
	harFlereGjeldendeIdenttyper,
} from '@/components/fagsystem/pdlf/form/partials/nyIdent/nyIdentUtils'
import styled from 'styled-components'

const CheckboxWrapper = styled.div`
	display: flex;

	&& {
		.dolly-form-input {
			min-width: 0;
			flex-grow: 0;
		}
	}
`

interface NyIdentProps {
	formMethods: UseFormReturn
}

export const NyIdent = ({ formMethods }: NyIdentProps) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const standardIdenttype = getStandardIdenttype(opts)

	const fjernHvisIkkeValgt = (name: string, checked: boolean) => {
		if (!checked) {
			formMethods.setValue(name, undefined)
		}
	}

	return (
		<>
			<NyIdentRegelInfo flereIdenttyper={harFlereGjeldendeIdenttyper(opts)} />
			<FormDollyFieldArray
				name="pdldata.person.nyident"
				header="Ny identitet"
				newEntry={getInitialNyIdent(opts?.identtype === 'NPID' ? 'PDL' : 'FREG', standardIdenttype)}
				canBeEmpty={false}
			>
				{(path: string) => {
					const nyIdentValg = Object.keys(formMethods.watch(path))
						.filter((key) => key !== 'eksisterendeIdent' && key !== 'kilde' && key !== 'master')
						.reduce((obj, key) => {
							obj[key] = formMethods.watch(path)[key]
							return obj
						}, {})
					const eksisterendePerson = formMethods.watch(`${path}.eksisterendePerson`)
					const { visSyntetisk, visId2032 } = getKonverterValg(opts, eksisterendePerson)

					const initialNyIdent = {
						...getInitialNyIdent(undefined, standardIdenttype),
						kilde: formMethods.watch(`${path}.kilde`),
						master: formMethods.watch(`${path}.master`),
					}

					const initialEksisterendePerson = {
						eksisterendeIdent: null,
						kilde: formMethods.watch(`${path}.kilde`),
						master: formMethods.watch(`${path}.master`),
					}

					return (
						<div className="flexbox--flex-wrap">
							<PdlPersonForm
								path={path}
								nyPersonPath={path}
								eksisterendePersonPath={`${path}.eksisterendeIdent`}
								label="NY IDENTITET"
								nyPersonLabel="Opprett ny ident"
								eksisterendePersonLabel="Velg eksisterende ident"
								formMethods={formMethods}
								nyIdentValg={nyIdentValg}
								initialNyIdent={initialNyIdent}
								initialEksisterendePerson={initialEksisterendePerson}
							>
								<CheckboxWrapper>
									<FormCheckbox
										name={`${path}.syntetisk`}
										id={`${path}.syntetisk`}
										label="Syntetisk"
										vis={visSyntetisk}
										afterChange={(checked) => fjernHvisIkkeValgt(`${path}.syntetisk`, checked)}
										checkboxMargin
									/>
									<FormCheckbox
										name={`${path}.id2032`}
										id={`${path}.id2032`}
										label="ID2032"
										vis={visId2032}
										afterChange={(checked) => fjernHvisIkkeValgt(`${path}.id2032`, checked)}
										checkboxMargin
									/>
								</CheckboxWrapper>
							</PdlPersonForm>
							<AvansertForm path={path} kanVelgeMaster={opts?.identtype !== 'NPID'} />
						</div>
					)
				}}
			</FormDollyFieldArray>
		</>
	)
}
