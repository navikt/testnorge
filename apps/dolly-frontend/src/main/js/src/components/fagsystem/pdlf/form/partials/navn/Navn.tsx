import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { getInitialNavn } from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Option } from '@/service/SelectOptionsOppslag'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash-es'
import { isEmpty } from 'lodash'
import { useContext, useEffect, useState } from 'react'
import { ArrowCirclepathIcon } from '@navikt/aksel-icons'
import { Button } from '@navikt/ds-react'
import styled from 'styled-components'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

type NavnTypes = {
	formMethods: any
	path?: string
	identtype?: string
}

const RefreshButton = styled(Button)`
	margin: 8px 0 0 -10px;
`

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
		?.sort((first, second) => (first.label > second.label ? 1 : -1))

	return _.uniqBy(navnOptions, 'label')
}

export const NavnForm = ({ formMethods, path, identtype }: NavnTypes) => {
	const errors = formMethods.formState.errors
	const [selectedFornavn, setSelectedFornavn] = useState(
		_.get(formMethods.getValues(), `${path}.alleFornavn`) || [],
	)

	const [selectedMellomnavn, setSelectedMellomnavn] = useState(
		_.get(formMethods.getValues(), `${path}.alleMellomnavn`) || [],
	)
	const [selectedEtternavn, setSelectedEtternavn] = useState(
		_.get(formMethods.getValues(), `${path}.alleEtternavn`) || [],
	)
	const [fornavnOptions, setFornavnOptions] = useState([])

	const [mellomnavnOptions, setMellomnavnOptions] = useState([])
	const [etternavnOptions, setetternavnOptions] = useState([])
	const { data, navnInfo, mutate } = useGenererNavn()

	if (!_.get(formMethods.getValues(), path)) {
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

	const { fornavn, mellomnavn, etternavn } = _.get(formMethods.getValues(), path)

	function getRefreshButton() {
		return (
			<RefreshButton
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
					<FormikSelect
						name={`${path}.alleFornavn`}
						label="Fornavn"
						placeholder={fornavn || 'Velg...'}
						value={selectedFornavn}
						options={fornavnOptions}
						afterChange={(change) => {
							const fornavn = change?.map((option: Option) => option.value)
							setSelectedFornavn(fornavn)
							formMethods.setValue(`${path}.fornavn`, fornavn?.join(' '))
						}}
						isMulti={true}
						size="grow"
						isClearable={false}
						fastfield={false}
						feil={
							_.get(errors, `${path}.fornavn`) && {
								feilmelding: _.get(errors, `${path}.fornavn`),
							}
						}
					/>
					{getRefreshButton()}
				</div>
				<div style={{ display: 'flex', alignItems: 'center' }}>
					<FormikSelect
						name={`${path}.alleMellomnavn`}
						label="Mellomnavn"
						placeholder={mellomnavn || 'Velg...'}
						options={mellomnavnOptions}
						afterChange={(change) => {
							const mellomnavn = change?.map((option: Option) => option.value)
							setSelectedMellomnavn(mellomnavn)
							formMethods.setValue(`${path}.mellomnavn`, mellomnavn?.join(' '))
						}}
						isDisabled={_.get(formMethods.getValues(), `${path}.hasMellomnavn`)}
						isMulti={true}
						size="grow"
						isClearable={true}
						fastfield={false}
					/>
					{getRefreshButton()}
				</div>
				<div style={{ display: 'flex', alignItems: 'center' }}>
					<FormikSelect
						name={`${path}.alleEtternavn`}
						label="Etternavn"
						placeholder={etternavn || 'Velg...'}
						options={etternavnOptions}
						afterChange={(change) => {
							const etternavn = change?.map((option: Option) => option.value)
							setSelectedEtternavn(etternavn)
							formMethods.setValue(`${path}.etternavn`, etternavn.join(' '))
						}}
						isMulti={true}
						size="grow"
						isClearable={false}
						fastfield={false}
						feil={
							_.get(errors, `${path}.etternavn`) && {
								feilmelding: _.get(errors, `${path}.etternavn`),
							}
						}
					/>
					{getRefreshButton()}
				</div>
			</div>
			<div className="flexbox--flex-wrap">
				<FormikCheckbox
					name={`${path}.hasMellomnavn`}
					label="Har tilfeldig mellomnavn"
					isDisabled={!isEmpty(selectedMellomnavn)}
					checkboxMargin
				/>
				<DatepickerWrapper>
					<FormikDatepicker
						name={`${path}.gyldigFraOgMed`}
						label="Gyldig f.o.m. dato"
						fastfield={false}
					/>
				</DatepickerWrapper>
			</div>
			<AvansertForm path={path} kanVelgeMaster={identtype !== 'NPID'} />
		</>
	)
}

export const Navn = ({ formMethods }: NavnTypes) => {
	const opts = useContext(BestillingsveilederContext)
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.navn'}
				header="Navn"
				newEntry={getInitialNavn(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string) => (
					<NavnForm formMethods={formMethods} path={path} identtype={opts?.identtype} />
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
