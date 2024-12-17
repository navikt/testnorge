import { Tabs } from '@navikt/ds-react'
import React from 'react'
import styled from 'styled-components'
import { FileCodeIcon, KeyVerticalIcon } from '@navikt/aksel-icons'
import PrettyCode, { SupportedPrettyCodeLanguages } from '@/components/codeView/PrettyCode'

const TabsVisningFormatter = styled.div`
	width: 100%;

	&& {
		.navds-tabs__tablist-wrapper {
			margin-bottom: 20px;
		}
	}

	&&& {
		button {
			position: static;
		}
	}
`

export const TabsVisning = ({ children, kildedata }: any) => {
	if (!kildedata) {
		return <div className="person-visning_content">{children}</div>
	}
	const kildedataJson = JSON.parse(kildedata)
	const kildedataPretty = JSON.stringify(kildedataJson, null, 2)

	return (
		<TabsVisningFormatter>
			<Tabs defaultValue="nokkelinfo" size="small">
				<Tabs.List>
					<Tabs.Tab
						value="nokkelinfo"
						label="Nøkkelinformasjon"
						icon={<KeyVerticalIcon title="a11y-title" fontSize="1.5rem" />}
					/>
					<Tabs.Tab
						value="kildedata"
						label="Kildedata"
						icon={<FileCodeIcon title="a11y-title" fontSize="1.5rem" />}
					/>
				</Tabs.List>
				<Tabs.Panel value="nokkelinfo">
					<div className="person-visning_content">{children}</div>
				</Tabs.Panel>
				<Tabs.Panel
					value="kildedata"
					style={{
						width: '100%',
						maxHeight: '600px',
						overflowX: 'auto',
						scrollbarWidth: 'thin',
						marginBottom: '15px',
					}}
				>
					<PrettyCode
						language={SupportedPrettyCodeLanguages.JSON}
						codeString={kildedataPretty}
						wrapLongLines
					/>
				</Tabs.Panel>
			</Tabs>
		</TabsVisningFormatter>
	)
}
