import React from 'react'
import { Alert, Tabs } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledTabs = styled(Tabs)`
	margin-top: -10px;
	margin-bottom: 20px;
`

const StyledPanel = styled(Tabs.Panel)`
	margin-top: 10px;
`

const BestiltMiljoTab = styled(Tabs.Tab)`
	color: #06893a;
	&&& {
		.navds-tabs__tab-inner {
			color: #06893a;
		}
	}
`

export const MiljoTabs = ({ bestilteMiljoer, forsteMiljo, data, children }) => {
	return (
		<StyledTabs size="small" defaultValue={forsteMiljo}>
			<Tabs.List>
				{data.map((miljoData) => {
					if (bestilteMiljoer?.includes(miljoData?.miljo)) {
						return (
							<BestiltMiljoTab
								key={miljoData?.miljo}
								value={miljoData?.miljo}
								label={miljoData?.miljo?.toUpperCase()}
							/>
						)
					}
					return (
						<Tabs.Tab
							key={miljoData?.miljo}
							value={miljoData?.miljo}
							label={miljoData?.miljo?.toUpperCase()}
						/>
					)
				})}
			</Tabs.List>
			{data.map((miljoData) => {
				return (
					<StyledPanel key={miljoData?.miljo} value={miljoData?.miljo}>
						{miljoData?.data?.length > 0 ? (
							React.cloneElement(children, { data: miljoData?.data })
						) : (
							<Alert variant="info" size="small" inline>
								Fant ingen data i dette miljøet
							</Alert>
						)}
					</StyledPanel>
				)
			})}
		</StyledTabs>
	)
}
