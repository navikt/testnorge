import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { useParams } from 'react-router'
import { DollyApi } from '@/service/Api'
import { useAsync } from 'react-use'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useContext, useEffect, useState } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { ToggleGroup } from '@navikt/ds-react'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'
import { initialPdlPerson } from '@/components/fagsystem/pdlf/form/initialValues'

interface PdlPersonValues {
	path: string
	nyPersonPath: string
	eksisterendePersonPath: string
	fullmektigsNavnPath?: string
	label: string
	formMethods: UseFormReturn
	nyIdentValg?: NyIdent | null
	eksisterendeNyPerson?: Option | null
	initialNyIdent?: any
	initialEksisterendePerson?: any
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
	path,
	nyPersonPath,
	eksisterendePersonPath,
	fullmektigsNavnPath,
	label,
	formMethods,
	nyIdentValg = null,
	eksisterendeNyPerson = null,
	initialNyIdent = null,
	initialEksisterendePerson = null,
}: PdlPersonValues) => {
	const { gruppeId: gruppeIdParam } = useParams()
	const formGruppeId = formMethods.watch('gruppeId')
	const gruppeId = formGruppeId || gruppeIdParam
	const gruppe = useAsync(async () => {
		return await DollyApi.getGruppeById(gruppeId)
	}, [])

	const getType = () => {
		if (formMethods.watch(`${path}.eksisterendePerson`)) {
			return PersonType.EKSISTERENDE_PERSON
		}
		const eksisterende = formMethods.watch(eksisterendePersonPath)
		return eksisterende ? PersonType.EKSISTERENDE_PERSON : PersonType.NY_PERSON
	}

	//@ts-ignore
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const [type, setType] = useState(getType())

	const gruppeIdenter = gruppe?.value?.data?.identer?.map((person) => person.ident)

	const isTestnorgeIdent = opts?.identMaster === 'PDL'

	const parentPath = path.substring(0, path.lastIndexOf('.'))

	useEffect(() => {
		setType(getType())
	}, [formMethods.watch(parentPath)?.length])

	const handleTypeChange = (value: string) => {
		setType(value)
		formMethods.setValue(
			nyPersonPath,
			value === PersonType.NY_PERSON ? initialNyIdent || initialPdlPerson : undefined,
		)
		if (value === PersonType.EKSISTERENDE_PERSON && initialEksisterendePerson) {
			formMethods.setValue(path, initialEksisterendePerson)
		} else {
			formMethods.setValue(
				eksisterendePersonPath,
				value === PersonType.EKSISTERENDE_PERSON ? eksisterendeNyPerson?.value : undefined,
			)
		}
		if (value === PersonType.NY_PERSON) {
			formMethods.setValue(`${path}.fullmektigsNavn`, undefined)
		}
		if (path) {
			formMethods.setValue(`${path}.eksisterendePerson`, value === PersonType.EKSISTERENDE_PERSON)
		}
		formMethods.trigger(path)
	}

	return (
		<>
			{!isTestnorgeIdent && (
				<>
					<StyledToggleGroup
						size={'small'}
						value={type}
						onChange={(type) => handleTypeChange(type)}
						label={'Personvalg'}
						key={'toggle-' + path}
					>
						<ToggleGroup.Item key={path + PersonType.NY_PERSON} value={PersonType.NY_PERSON}>
							<Icon
								key={path + PersonType.NY_PERSON}
								size={13}
								kind={type === PersonType.NY_PERSON ? 'person-plus' : 'person-plus-fill'}
							/>
							{'Opprett ny person'}
						</ToggleGroup.Item>
						<ToggleGroup.Item
							key={path + PersonType.EKSISTERENDE_PERSON}
							value={PersonType.EKSISTERENDE_PERSON}
						>
							<Icon
								key={path + PersonType.EKSISTERENDE_PERSON}
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
							key={'person-' + path}
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
						{(type === PersonType.EKSISTERENDE_PERSON || isTestnorgeIdent) && (
							<PdlEksisterendePerson
								eksisterendePersonPath={eksisterendePersonPath}
								label={label}
								formMethods={formMethods}
								eksisterendeNyPerson={eksisterendeNyPerson}
								fullmektigsNavnPath={fullmektigsNavnPath}
								disabled={opts?.antall > 1}
								key={'eksisterendePerson-' + path}
							/>
						)}
					</div>
				}
			</>
		</>
	)
}
