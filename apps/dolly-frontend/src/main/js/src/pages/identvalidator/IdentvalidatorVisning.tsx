import { Alert, Box, Table } from '@navikt/ds-react'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'

export const IdentvalidatorVisning = ({ data }) => {
	if (!data) {
		return null
	}

	const getIcon = (isValid: boolean) => {
		return isValid ? 'success' : 'error'
	}

	const mappedData = [
		{ label: 'Er gyldig', value: oversettBoolean(data.erGyldig), icon: getIcon(data.erGyldig) },
		{
			label: 'Er ny ident (2032)',
			value: oversettBoolean(data.erPersonnummer2032),
			icon: getIcon(data.erPersonnummer2032),
		},
		{
			label: 'Er syntetisk',
			value: oversettBoolean(data.erSyntetisk),
			icon: getIcon(data.erSyntetisk),
		},
		{
			label: 'Er Testnorge-ident',
			value: oversettBoolean(data.erTestnorgeIdent),
			icon: getIcon(data.erTestnorgeIdent),
		},
		// { label: 'Ident', value: data.ident },
		{ label: 'Identtype', value: data.identtype },
		{ label: 'Fødselsdato', value: formatDate(data.foedselsdato) },
		{ label: 'Kjønn', value: data.kjoenn },
		{ label: 'Kommentar', value: data.kommentar },
		{ label: 'Feilmelding', value: data.feilmelding },
	]

	return (
		<Box
			padding="6"
			// background={'surface-default'}
			background={data.erGyldig ? 'surface-success-subtle' : 'surface-danger-subtle'}
			borderRadius="large"
			borderWidth="2"
			borderColor={data.erGyldig ? 'border-success' : 'border-danger'}
		>
			<h2 style={{ paddingLeft: '8px', marginTop: '8px' }}>Validering av ident {data.ident}</h2>
			<Table>
				<Table.Body>
					{mappedData.map((item, index) => {
						if (item.value === null || item.value === undefined || item.value === '') {
							return null
						}
						return (
							<Table.Row
								key={index + item.label}
								shadeOnHover={false}
								// style={{ verticalAlign: 'top' }}
							>
								<Table.DataCell width="30%">
									<strong>{item.label}</strong>
								</Table.DataCell>
								<Table.DataCell>
									{item.icon ? (
										<Alert variant={item.icon} inline>
											{item.value}
										</Alert>
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
