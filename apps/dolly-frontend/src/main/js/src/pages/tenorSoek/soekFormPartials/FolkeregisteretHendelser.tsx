import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React from 'react'

export const FolkeregisteretHendelser = ({ handleChange }: any) => {
	const { domain: hendelseOptions } = useTenorDomain('Hendelse')

	return (
		<SoekKategori>
			<FormSelect
				name="hendelser.hendelse"
				options={createOptions(hendelseOptions?.data, true)}
				size="xlarge"
				label="Har hatt hendelse"
				onChange={(val: any) => handleChange(val?.value || null, 'hendelser.hendelse')}
			/>
			<FormSelect
				name="hendelser.sisteHendelse"
				options={createOptions(hendelseOptions?.data, true)}
				size="xlarge"
				label="Siste hendelse"
				onChange={(val: any) => handleChange(val?.value || null, 'hendelser.sisteHendelse')}
			/>
		</SoekKategori>
	)
}
