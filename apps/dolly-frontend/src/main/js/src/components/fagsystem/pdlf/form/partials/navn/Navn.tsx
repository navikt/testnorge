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

type NavnTypes = {
	formikBag: FormikProps<{}>
	path?: string
}

const concatNavnMedTidligereValgt = (type, navnInfo, selectedFornavn) => {
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

export const NavnForm = ({ formikBag, path }: NavnTypes) => {
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
	const navnInfo = SelectOptionsOppslag.hentPersonnavn()

	useEffect(() => {
		setFornavnOptions(concatNavnMedTidligereValgt('fornavn', navnInfo, selectedFornavn))
		setMellomnavnOptions(concatNavnMedTidligereValgt('mellomnavn', navnInfo, selectedMellomnavn))
		setetternavnOptions(concatNavnMedTidligereValgt('etternavn', navnInfo, selectedEtternavn))
	}, [navnInfo])

	const { fornavn, mellomnavn, etternavn } = _.get(formikBag?.values, path)

	return (
		<>
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${path}.alleFornavn`}
					label="Fornavn"
					placeholder={fornavn || 'Velg..'}
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
				<FormikSelect
					name={`${path}.alleMellomnavn`}
					label="Mellomnavn"
					placeholder={mellomnavn || 'Velg..'}
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
				<FormikSelect
					name={`${path}.alleEtternavn`}
					label="Etternavn"
					placeholder={etternavn || 'Velg..'}
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
			</div>
			<FormikCheckbox
				name={`${path}.hasMellomnavn`}
				label="Har tilfeldig mellomnavn"
				isDisabled={!isEmpty(selectedMellomnavn)}
			/>
			<AvansertForm path={path} kanVelgeMaster={true} />
		</>
	)
}

export const Navn = ({ formikBag }: NavnTypes) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.navn'}
				header="Navn"
				newEntry={initialNavn}
				canBeEmpty={false}
			>
				{(path: string) => <NavnForm formikBag={formikBag} path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
