import { Box, Table } from '@navikt/ds-react'
import { formatDate } from '@/utils/DataFormatter'
import { getIcon, IconComponent } from '@/pages/identvalidator/utils'

interface IdentvalidatorData {
	erGyldig: boolean
	erPersonnummer2032: boolean
	erSyntetisk: boolean
	erTestnorgeIdent: boolean
	erIProd: boolean
	identtype: string
	foedselsdato: string
	kjoenn: string
	kommentar: string
	feilmelding: string
	ident: string
}

interface IdentvalidatorVisningProps {
	data: IdentvalidatorData
}

export const IdentvalidatorVisning = ({ data }: IdentvalidatorVisningProps) => {
	if (!data) {
		return null
	}

	const mappedData = [
		{
			label: 'Er gyldig',
			value: data.erGyldig,
			icon: getIcon(data.erGyldig, true),
		},
		{
			label: 'Er i prod',
			value: data.erIProd,
			icon: data.erIProd ? 'warning' : 'none',
		},
		{
			label: 'Er syntetisk',
			value: data.erSyntetisk,
			icon: getIcon(data.erSyntetisk),
		},
		{
			label: 'Er Testnorge-ident',
			value: data.erTestnorgeIdent,
			icon: getIcon(data.erTestnorgeIdent),
		},
		{
			label: 'Er ny ident (2032)',
			value: data.erPersonnummer2032,
			icon: getIcon(data.erPersonnummer2032),
		},
		{ label: 'Identtype', value: data.identtype },
		{ label: 'Fødselsdato', value: formatDate(data.foedselsdato) },
		{ label: 'Kjønn', value: data.kjoenn },
		{ label: 'Kommentar', value: data.kommentar },
		{ label: 'Feilmelding', value: data.feilmelding },
	]

	return (
		<Box padding="space-16" background={data.feilmelding ? 'danger-moderate' : 'success-moderate'}>
			<Table>
				<Table.Body>
					{mappedData.map((item) => {
						if (item.value === null || item.value === undefined || item.value === '') {
							return null
						}
						return (
							<Table.Row key={item.label} shadeOnHover={false}>
								<Table.DataCell width="30%">
									<strong>{item.label}</strong>
								</Table.DataCell>
								<Table.DataCell>
									{item.icon ? (
										<IconComponent isValid={item.value} iconType={item.icon} />
									) : (
										item.value
									)}
								</Table.DataCell>
							</Table.Row>
						)
					})}
				</Table.Body>
			</Table>
		</Box>
	)
}
