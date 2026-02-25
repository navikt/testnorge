import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { getInitialNavn } from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Option } from '@/service/SelectOptionsOppslag'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash-es'
import { useContext, useEffect, useState } from 'react'
import { ArrowCirclepathIcon } from '@navikt/aksel-icons'
import { Button } from '@navikt/ds-react'
import styled from 'styled-components'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

type NavnTypes = {
	formMethods: any
	path?: string
	identtype?: string
	identMaster?: string
}

const RefreshButton = styled(Button)`
	margin: 8px 0 0 -10px;
`

const manuelleNavn = [
	{ adjektiv: 'Lillegull', adverb: 'Lillesøster', substantiv: 'Navnesen' },
	{ adjektiv: 'Sussebass', adverb: 'Lillebror', substantiv: 'Etternavnesen' },
]

const concatNavnMedTidligereValgt = (type, navnInfo, selectedNavn) => {
	if (!navnInfo) {
		return []
	}
	const navnOptions = SelectOptionsFormat.formatOptions(type, navnInfo)
		.concat(
			selectedNavn?.map((navn) => ({
				value: navn,
				label: navn,
			})),
		)
		?.sort?.((first, second) => (first.label > second.label ? 1 : -1))

	return _.uniqBy(navnOptions, 'label')
}

export const NavnForm = ({ formMethods, path, identtype, identMaster }: NavnTypes) => {
	const [selectedFornavn, setSelectedFornavn] = useState(
		formMethods.watch(`${path}.alleFornavn`) || [],
	)

	const [selectedMellomnavn, setSelectedMellomnavn] = useState(
		formMethods.watch(`${path}.alleMellomnavn`) || [],
	)
	const [selectedEtternavn, setSelectedEtternavn] = useState(
		formMethods.watch(`${path}.alleEtternavn`) || [],
	)
	const [fornavnOptions, setFornavnOptions] = useState([])

	const [mellomnavnOptions, setMellomnavnOptions] = useState([])
	const [etternavnOptions, setetternavnOptions] = useState([])
	const { data, navnInfo, mutate } = useGenererNavn(manuelleNavn)

	if (!formMethods.watch(path)) {
		return null
	}

	const refreshNavn = () => {
		mutate()
	}

	useEffect(() => {
		setFornavnOptions(concatNavnMedTidligereValgt('fornavn', navnInfo, selectedFornavn))
		setMellomnavnOptions(concatNavnMedTidligereValgt('mellomnavn', navnInfo, selectedMellomnavn))
		setetternavnOptions(concatNavnMedTidligereValgt('etternavn', navnInfo, selectedEtternavn))
	}, [data])

	const { fornavn, mellomnavn, etternavn } = formMethods.watch(path)

	function getRefreshButton() {
		return (
			<RefreshButton
				type="button"
				title={'Hent nye navn'}
				size={'small'}
				onClick={refreshNavn}
				icon={<ArrowCirclepathIcon />}
				variant={'tertiary'}
			></RefreshButton>
		)
	}

	return (
		<>
			<div className="flexbox--full-width">
				<div style={{ display: 'flex', alignItems: 'center' }}>
					<FormSelect
						name={`${path}.alleFornavn`}
						label="Fornavn"
						placeholder={fornavn || 'Velg ...'}
						value={selectedFornavn}
						options={fornavnOptions}
						afterChange={(change) => {
							const fornavn = change?.map((option: Option) => option.value)
							setSelectedFornavn(fornavn)
							formMethods.setValue(`${path}.fornavn`, fornavn?.join(' '))
							formMethods.trigger(path)
						}}
						isMulti={true}
						size="grow"
						isClearable={false}
					/>
					{getRefreshButton()}
				</div>
				<div style={{ display: 'flex', alignItems: 'center' }}>
					<FormSelect
						name={`${path}.alleMellomnavn`}
						label="Mellomnavn"
						placeholder={mellomnavn || 'Velg ...'}
						options={mellomnavnOptions}
						afterChange={(change) => {
							const mellomnavn = change?.map((option: Option) => option.value)
							setSelectedMellomnavn(mellomnavn)
							formMethods.setValue(`${path}.mellomnavn`, mellomnavn?.join(' '))
							formMethods.trigger(path)
						}}
						isDisabled={formMethods.watch(`${path}.hasMellomnavn`)}
						isMulti={true}
						size="grow"
						isClearable={true}
					/>
					{getRefreshButton()}
				</div>
				<div style={{ display: 'flex', alignItems: 'center' }}>
					<FormSelect
						name={`${path}.alleEtternavn`}
						label="Etternavn"
						placeholder={etternavn || 'Velg ...'}
						options={etternavnOptions}
						afterChange={(change) => {
							const etternavn = change?.map((option: Option) => option.value)
							setSelectedEtternavn(etternavn)
							formMethods.setValue(`${path}.etternavn`, etternavn.join(' '))
							formMethods.trigger(path)
						}}
						isMulti={true}
						size="grow"
						isClearable={false}
					/>
					{getRefreshButton()}
				</div>
			</div>
			<div className="flexbox--flex-wrap">
				<FormCheckbox
					name={`${path}.hasMellomnavn`}
					id={`${path}.hasMellomnavn`}
					label="Har tilfeldig mellomnavn"
					disabled={!_.isEmpty(selectedMellomnavn)}
					checkboxMargin
					wrapperSize="shrink"
				/>
				<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m. dato" />
			</div>
			<AvansertForm path={path} kanVelgeMaster={identMaster !== 'PDL' && identtype !== 'NPID'} />
		</>
	)
}

export const Navn = ({ formMethods }: NavnTypes) => {
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.navn'}
				header="Navn"
				newEntry={getInitialNavn(
					opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
				)}
				canBeEmpty={false}
			>
				{(path: string) => (
					<NavnForm
						formMethods={formMethods}
						path={path}
						identtype={opts?.identtype}
						identMaster={opts?.identMaster}
					/>
				)}
			</FormDollyFieldArray>
		</div>
	)
}
