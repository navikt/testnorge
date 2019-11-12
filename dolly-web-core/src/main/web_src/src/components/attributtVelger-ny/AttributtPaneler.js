import React, { Fragment } from 'react'
import Panel from '~/components/ui/panel/Panel'
import TilgjengeligeMiljoer from '~/components/tilgjengeligeMiljoer/TilgjengeligeMiljoer'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const AttributtPaneler = ({ attributter, checkAttributter }) => (
	<div className="attributt-velger_panels">
		{attributter.map(attr => (
			<AttributtKategori
				key={attr.panel}
				navn={attr.panel}
				kategori={attr.kategori}
				infotekst={attr.infotekst}
				tilgjengeligeMiljoeEndepunkt={attr.tilgjengeligeMiljoeEndepunkt}
				checkAttributter={checkAttributter}
			>
				{attr.kategori.map(kategori => (
					<Fragment key={kategori.navn}>
						{attr.kategori.length > 1 && <h3>{kategori.navn}</h3>}
						<div className="attributt-velger_panelsubcontent">
							{kategori.sub.map(input => (
								<FormikCheckbox
									key={input.name}
									size="grow"
									label={input.label}
									name={`attributter.${input.name}`}
								/>
							))}
						</div>
					</Fragment>
				))}
			</AttributtKategori>
		))}
	</div>
)

const AttributtKategori = ({
	navn,
	kategori,
	infotekst,
	tilgjengeligeMiljoeEndepunkt,
	checkAttributter,
	children
}) => {
	const allePanelAttrs = kategori
		.map(x => x.sub)
		.flat()
		.map(y => y.name)

	const checkAll = () => checkAttributter(allePanelAttrs, true)
	const unCheckAll = () => checkAttributter(allePanelAttrs, false)

	const tooltip = () => {
		if (!infotekst && !tilgjengeligeMiljoeEndepunkt) return false
		return (
			<Fragment>
				{infotekst}
				<br />
				<TilgjengeligeMiljoer endepunkt={tilgjengeligeMiljoeEndepunkt} />
			</Fragment>
		)
	}

	return (
		<Panel
			key={navn}
			heading={navn}
			startOpen
			checkAttributeArray={checkAll}
			uncheckAttributeArray={unCheckAll}
			informasjonstekst={tooltip()}
		>
			<fieldset name={navn}>{children}</fieldset>
		</Panel>
	)
}
