import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { useParams } from 'react-router-dom'
import { DollyApi } from '@/service/Api'
import { useAsync } from 'react-use'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useContext, useEffect, useState } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { ToggleGroup } from '@navikt/ds-react'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'
import { initialPdlPerson } from '@/components/fagsystem/pdlf/form/initialValues'

interface PdlPersonValues {
	nyPersonPath: string
	eksisterendePersonPath: string
	label: string
	formMethods: UseFormReturn
	nyIdentValg?: NyIdent
	eksisterendeNyPerson?: Option
}

const StyledToggleGroup = styled(ToggleGroup)`
	&&&& {
		div {
			background-color: #ffffff;
		}

		button {
			margin-right: unset;
		}
	}

	margin: 5px 0;
`

const PersonType = {
	NY_PERSON: 'nyPerson',
	EKSISTERENDE_PERSON: 'eksisterendePerson',
}

export const PdlPersonForm = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formMethods,
	nyIdentValg = null,
	eksisterendeNyPerson = null,
}: PdlPersonValues) => {
	const { gruppeId } = useParams()
	const gruppe = useAsync(async () => {
		return await DollyApi.getGruppeById(gruppeId)
	}, [])
	//@ts-ignore
	const opts: any = useContext(BestillingsveilederContext)
	const [type, setType] = useState(
		formMethods.watch('vergemaal.vergeIdent')
			? PersonType.EKSISTERENDE_PERSON
			: PersonType.NY_PERSON,
	)

	const gruppeIdenter = gruppe?.value?.data?.identer?.map((person) => person.ident)

	const isTestnorgeIdent = opts?.identMaster === 'PDL'

	useEffect(() => {
		formMethods.setValue(nyPersonPath, type === PersonType.NY_PERSON ? initialPdlPerson : undefined)
		formMethods.setValue(
			eksisterendePersonPath,
			type === PersonType.EKSISTERENDE_PERSON ? eksisterendeNyPerson?.value : undefined,
		)
		formMethods.trigger()
	}, [type])

	return (
		<>
			{!isTestnorgeIdent && (
				<>
					<StyledToggleGroup size={'small'} value={type} onChange={setType} label={'Personvalg'}>
						<ToggleGroup.Item key={PersonType.NY_PERSON} value={PersonType.NY_PERSON}>
							<Icon
								key={PersonType.NY_PERSON}
								size={13}
								kind={type === PersonType.NY_PERSON ? 'person-plus' : 'person-plus-fill'}
							/>
							{'Opprett ny person'}
						</ToggleGroup.Item>
						<ToggleGroup.Item
							key={PersonType.EKSISTERENDE_PERSON}
							value={PersonType.EKSISTERENDE_PERSON}
						>
							<Icon
								key={PersonType.EKSISTERENDE_PERSON}
								size={13}
								kind={type === PersonType.EKSISTERENDE_PERSON ? 'person' : 'person-fill'}
							/>
							{'Velg eksisterende person'}
						</ToggleGroup.Item>
					</StyledToggleGroup>
					{type === PersonType.NY_PERSON && (
						<PdlNyPerson
							nyPersonPath={nyPersonPath}
							eksisterendePersonPath={eksisterendePersonPath}
							formMethods={formMethods}
							erNyIdent={nyIdentValg !== null}
							gruppeIdenter={gruppeIdenter}
							eksisterendeNyPerson={eksisterendeNyPerson}
						/>
					)}
				</>
			)}
			<>
				{
					<div
						title={
							(opts?.antall > 1 && 'Valg er kun tilgjengelig for individ, ikke for gruppe') || ''
						}
					>
						{type === PersonType.EKSISTERENDE_PERSON && (
							<PdlEksisterendePerson
								nyPersonPath={nyPersonPath}
								eksisterendePersonPath={eksisterendePersonPath}
								label={label}
								formMethods={formMethods}
								nyIdentValg={nyIdentValg}
								eksisterendeNyPerson={eksisterendeNyPerson}
								disabled={opts?.antall > 1}
							/>
						)}
					</div>
				}
			</>
		</>
	)
}
