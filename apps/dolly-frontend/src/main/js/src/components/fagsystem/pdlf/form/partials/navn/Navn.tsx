import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialNavn } from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Option, SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash-es'
import { FormikProps } from 'formik'
import { isEmpty } from 'lodash'
import { useEffect, useState } from 'react'
import { DollyApi } from '@/service/Api'
import { ArrowCirclepathIcon } from '@navikt/aksel-icons'
import { Button } from '@navikt/ds-react'
import styled from 'styled-components'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

type NavnTypes = {
	formikBag: FormikProps<{}>
	path?: string
}

const RefreshButton = styled(Button)`
	margin: 8px 0 0 -10px;
`

const concatNavnMedTidligereValgt = (type, navnInfo, selectedFornavn) => {
	if (!navnInfo) {
		return []
	}
	const navnOptions = SelectOptionsOppslag.formatOptions(type, navnInfo)
		.concat(
			selectedFornavn?.map((navn) => ({
				value: navn,
				label: navn,
			}))
		)
		?.sort((first, second) => (first.label > second.label ? 1 : -1))

	return _.uniqBy(navnOptions, 'label')
}

export const NavnForm = ({ formikBag, path, idx, personValues }: NavnTypes) => {
	if (!_.get(formikBag?.values, path)) {
		return null
	}

	const [selectedFornavn, setSelectedFornavn] = useState(
		_.get(formikBag?.values, `${path}.alleFornavn`) || []
	)
	const [selectedMellomnavn, setSelectedMellomnavn] = useState(
		_.get(formikBag?.values, `${path}.alleMellomnavn`) || []
	)
	const [selectedEtternavn, setSelectedEtternavn] = useState(
		_.get(formikBag?.values, `${path}.alleEtternavn`) || []
	)

	const [fornavnOptions, setFornavnOptions] = useState([])
	const [mellomnavnOptions, setMellomnavnOptions] = useState([])
	const [etternavnOptions, setetternavnOptions] = useState([])
	const [navnInfo, setNavnInfo] = useState(null)

	function refreshNavn() {
		DollyApi.getPersonnavn().then((result) => setNavnInfo({ value: result, loading: false }))
	}

	useEffect(() => {
		refreshNavn()
	}, [])

	useEffect(() => {
		setFornavnOptions(concatNavnMedTidligereValgt('fornavn', navnInfo, selectedFornavn))
		setMellomnavnOptions(concatNavnMedTidligereValgt('mellomnavn', navnInfo, selectedMellomnavn))
		setetternavnOptions(concatNavnMedTidligereValgt('etternavn', navnInfo, selectedEtternavn))
	}, [navnInfo])

	const { fornavn, mellomnavn, etternavn } = _.get(formikBag?.values, path)

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

	const isLast = _.get(formikBag?.values, 'pdldata.person.navn')?.length === idx + 1

	const gjeldendeNavnId = _.get(personValues, 'navn[0].id')
	const erGjeldendeNavn =
		gjeldendeNavnId &&
		_.get(formikBag.values, 'navn.id') === gjeldendeNavnId &&
		!_.get(formikBag.values, 'navn.folkeregistermetadata.opphoerstidspunkt')

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
							formikBag.setFieldValue(`${path}.fornavn`, fornavn?.join(' '))
						}}
						isMulti={true}
						size="grow"
						isClearable={false}
						fastfield={false}
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
							formikBag.setFieldValue(`${path}.mellomnavn`, mellomnavn?.join(' '))
						}}
						isDisabled={_.get(formikBag?.values, `${path}.hasMellomnavn`)}
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
							formikBag.setFieldValue(`${path}.etternavn`, etternavn.join(' '))
						}}
						isMulti={true}
						size="grow"
						isClearable={false}
						fastfield={false}
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
						name={`${path}.folkeregistermetadata.opphoerstidspunkt`}
						label="Opphørt dato"
						disabled={isLast || erGjeldendeNavn}
						fastfield={false}
					/>
				</DatepickerWrapper>
			</div>
			<AvansertForm path={path} kanVelgeMaster={true} />
		</>
	)
}

export const Navn = ({ formikBag }: NavnTypes) => {
	const handleRemoveEntry = (idx: number) => {
		const navnListe = _.get(formikBag?.values, 'pdldata.person.navn')
		navnListe.splice(idx, 1)
		const lastIndex = navnListe?.length - 1

		formikBag.setFieldValue('pdldata.person.navn', navnListe)
		formikBag.setFieldValue(
			`pdldata.person.navn[${lastIndex}].folkeregistermetadata.opphoerstidspunkt`,
			null
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.navn'}
				header="Navn"
				newEntry={initialNavn}
				canBeEmpty={false}
				handleRemoveEntry={handleRemoveEntry}
			>
				{(path: string, idx: number) => <NavnForm formikBag={formikBag} path={path} idx={idx} />}
			</FormikDollyFieldArray>
		</div>
	)
}
