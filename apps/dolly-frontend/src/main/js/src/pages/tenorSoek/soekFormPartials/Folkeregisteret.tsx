import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { SyntheticEvent } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const Folkeregisteret = ({ formikBag, handleChange, handleChangeList, getValue }: any) => {
	const { domain: identifikatorTypeOptions } = useTenorDomain('IdentifikatorType')
	const { domain: kjoennOptions } = useTenorDomain('Kjoenn')

	return (
		<SoekKategori>
			<FormikTextInput
				name="identifikator"
				label="Fødselsnummer / D-nummer"
				onBlur={(val: SyntheticEvent) => handleChange(val?.target?.value || null, 'identifikator')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikSelect
				name="identifikatorType"
				options={createOptions(identifikatorTypeOptions?.data)}
				// size="medium"
				label="Identifikatortype"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'identifikatorType')}
				value={getValue('identifikatorType')}
			/>
			<FormikDatepicker
				name="foedselsdato.fraOgMed"
				label="Fødselsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.fraOgMed')}
				date={getValue('foedselsdato.fraOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikDatepicker
				name="foedselsdato.tilOgMed"
				label="Fødselsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.tilOgMed')}
				date={getValue('foedselsdato.tilOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikDatepicker
				name="doedsdato.fraOgMed"
				label="Dødsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.fraOgMed')}
				date={getValue('doedsdato.fraOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikDatepicker
				name="doedsdato.tilOgMed"
				label="Dødsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.tilOgMed')}
				date={getValue('doedsdato.tilOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikSelect
				name="kjoenn"
				options={createOptions(kjoennOptions?.data)}
				// size="medium"
				label="Kjønn"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'kjoenn')}
				value={getValue('kjoenn')}
			/>
		</SoekKategori>
	)
}
