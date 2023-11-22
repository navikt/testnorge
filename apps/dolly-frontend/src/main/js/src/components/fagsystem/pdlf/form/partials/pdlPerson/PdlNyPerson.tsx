import React, { useContext } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as _ from 'lodash'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface PdlNyPersonValues {
	nyPersonPath: string
	eksisterendePersonPath?: string
	formMethods: UseFormReturn
	erNyIdent?: boolean
	gruppeIdenter?: Array<string>
	eksisterendeNyPerson?: Option
}

export const PdlNyPerson = ({
	nyPersonPath,
	eksisterendePersonPath,
	formMethods,
	erNyIdent = false,
	gruppeIdenter,
	eksisterendeNyPerson = null,
}: PdlNyPersonValues) => {
	const opts = useContext(BestillingsveilederContext)
	const isLeggTil = opts?.is?.leggTil
	const disableAlder =
		_.get(formMethods.getValues(), `${nyPersonPath}.foedtEtter`) != null ||
		_.get(formMethods.getValues(), `${nyPersonPath}.foedtFoer`) != null

	const disableFoedtDato = !['', null].includes(
		_.get(formMethods.getValues(), `${nyPersonPath}.alder`),
	)

	const identtypeOptions =
		erNyIdent && isLeggTil
			? Options('identtype').filter((a) => a.value !== 'NPID')
			: Options('identtype')

	const eksisterendePerson = _.get(formMethods.getValues(), eksisterendePersonPath)

	const hasEksisterendePerson =
		eksisterendePerson &&
		(gruppeIdenter?.includes(eksisterendePerson) ||
			eksisterendePerson === eksisterendeNyPerson?.value ||
			_.get(formMethods.getValues(), 'vergemaal.vergeIdent') === eksisterendeNyPerson?.value ||
			_.get(formMethods.getValues(), 'sivilstand.relatertVedSivilstand') ===
				eksisterendeNyPerson?.value)

	return (
		<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
			<FormikSelect
				name={`${nyPersonPath}.identtype`}
				label="Identtype"
				options={identtypeOptions}
				isDisabled={hasEksisterendePerson}
			/>
			<FormikSelect
				name={`${nyPersonPath}.kjoenn`}
				label="Kjønn"
				options={Options('kjoenn')}
				isDisabled={hasEksisterendePerson}
			/>
			<FormikTextInput
				name={`${nyPersonPath}.alder`}
				type="number"
				label="Alder"
				isDisabled={disableAlder || hasEksisterendePerson}
			/>
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${nyPersonPath}.foedtEtter`}
					label="Født etter"
					disabled={disableFoedtDato || hasEksisterendePerson}
					maxDate={new Date()}
					fastfield={false}
				/>
				<FormikDatepicker
					name={`${nyPersonPath}.foedtFoer`}
					label="Født før"
					disabled={disableFoedtDato || hasEksisterendePerson}
					maxDate={new Date()}
					fastfield={false}
				/>
			</DatepickerWrapper>
			{!erNyIdent && (
				<FormikSelect
					name={`${nyPersonPath}.statsborgerskapLandkode`}
					label="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					size="large"
					isDisabled={hasEksisterendePerson}
				/>
			)}
			{!erNyIdent && (
				<FormikSelect
					name={`${nyPersonPath}.gradering`}
					label="Gradering"
					options={Options('gradering')}
					isDisabled={hasEksisterendePerson}
				/>
			)}
			<FormikCheckbox
				name={`${nyPersonPath}.nyttNavn.hasMellomnavn`}
				label="Har mellomnavn"
				isDisabled={hasEksisterendePerson}
				checkboxMargin={true}
			/>
		</div>
	)
}
