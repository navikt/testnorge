import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import ExpandButton from '~/components/ui/button/ExpandButton'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import ConfirmTooltip from '~/components/ui/confirmTooltip/ConfirmTooltip'
import './table.less'

class TableRow extends PureComponent {
	static propTypes = {
		expandComponent: PropTypes.node,
		navLink: PropTypes.func,
		editAction: PropTypes.func,
		deleteAction: PropTypes.func
	}

	state = {
		expanded: false
	}

	onRowClick = event => {
		const { expandComponent, navLink } = this.props
		if (expandComponent) return this.setState({ expanded: !this.state.expanded })

		if (navLink) return navLink()
	}

	render() {
		const {
			children,
			expandComponent,
			editAction,
			deleteAction,
			deleteMessage,
			navLink,
			groupId,
			...restProps
		} = this.props

		const rowProps = {
			onClick: this.onRowClick,
			...restProps
		}

		const rowClass = cn('dot-body-row', {
			expanded: this.state.expanded
		})

		const columnsClass = cn('dot-body-row-columns', {
			clickable: Boolean(expandComponent || navLink)
		})

		return (
			<div tabIndex={0} className={rowClass}>
				<div className={columnsClass} {...rowProps}>
					{children}
					<Table.Column className="dot-body-row-actioncolumn">
						{deleteAction && (
							<ConfirmTooltip
								className="flexbox--align-center"
								onClick={deleteAction}
								message={deleteMessage ? deleteMessage : 'SLETT?'}
							/>
						)}{' '}
						{groupId && <FavoriteButtonConnector hideLabel={true} groupId={groupId} />}
						{expandComponent && (
							<ExpandButton expanded={this.state.expanded} onClick={this.onRowClick} />
						)}
					</Table.Column>
				</div>
				{this.state.expanded && (
					<div className="dot-body-row-expandcomponent">{expandComponent}</div>
				)}
			</div>
		)
	}
}

class TableHeader extends PureComponent {
	static propTypes = {
		children: PropTypes.node
	}

	render() {
		const { children, ...restProps } = this.props
		return (
			<div tabIndex={0} className="dot-header" {...restProps}>
				{children}
			</div>
		)
	}
}

class TableColumn extends PureComponent {
	static propTypes = {
		width: PropTypes.oneOf([
			'10',
			'15',
			'20',
			'25',
			'30',
			'35',
			'40',
			'45',
			'50',
			'55',
			'60',
			'65',
			'70',
			'75',
			'80',
			'85',
			'90',
			'95'
		]),
		value: PropTypes.string
	}

	static defaultProps = {
		width: '10'
	}

	onSortHandler = () => {
		const { sortOrder, setSort, columnId } = this.props

		setSort({ id: columnId, order: sortOrder === 'asc' ? 'desc' : 'asc' })
	}

	render() {
		const {
			value,
			width,
			children,
			className,
			sortOrder,
			setSort,
			columnId,
			sortable,
			...restProps
		} = this.props
		const cssClass = cn('dot-column', `col${width}`, className)

		const render = value ? value : children
		return (
			<div className={cssClass} {...restProps}>
				{render}
			</div>
		)
	}
}

export default class Table extends PureComponent {
	static propTypes = {
		children: PropTypes.node.isRequired
	}

	static Header = TableHeader
	static Row = TableRow
	static Column = TableColumn

	render() {
		const { children, ...restProps } = this.props

		return (
			<div className="dot" {...restProps}>
				{children}
			</div>
		)
	}
}
